package ru.job4j.quartz;

import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.Properties;

import static org.quartz.JobBuilder.*;
import static org.quartz.TriggerBuilder.*;
import static org.quartz.SimpleScheduleBuilder.*;

public class AlertRabbit {
    public static void main(String[] args) {
        Properties properties = getProperties();
        int interval = Integer.parseInt(properties.getProperty("rabbit.interval"));
        try (Connection connection = initConnection()) {
            Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
            scheduler.start();
            JobDataMap dataMap = new JobDataMap();
            dataMap.put("connection", connection);
            JobDetail job = newJob(Rabbit.class)
                    .usingJobData(dataMap)
                    .build();
            SimpleScheduleBuilder times = simpleSchedule()
                    .withIntervalInSeconds(interval)
                    .repeatForever();
            Trigger trigger = newTrigger()
                    .startNow()
                    .withSchedule(times)
                    .build();
            scheduler.scheduleJob(job, trigger);
            Thread.sleep(10000);
            scheduler.shutdown();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static Connection initConnection() throws Exception {
        Properties properties = AlertRabbit.getProperties();
        return DriverManager.getConnection(properties.getProperty("url"),
                                           properties.getProperty("username"),
                                           properties.getProperty("password"));
    }

    private static Properties getProperties() {
        Properties properties = new Properties();
        try (InputStream in = AlertRabbit.class.getClassLoader()
                .getResourceAsStream("rabbit.properties")) {
            properties.load(in);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return properties;
    }

    public static class Rabbit implements Job {
        @Override
        public void execute(JobExecutionContext context) {
            Connection connection = (Connection) context.getJobDetail().getJobDataMap().get("connection");
            try (PreparedStatement statement = connection.prepareStatement(
                    "INSERT INTO rabbit (created_date) VALUES (?)"
            )) {
                statement.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now()));
                statement.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
