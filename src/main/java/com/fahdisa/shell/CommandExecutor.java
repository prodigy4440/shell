package com.fahdisa.shell;

import java.io.*;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CommandExecutor {

    private ExecutorService executor;
    private Process process;

    private Boolean isRunning;
    private LogListener listener;

    public CommandExecutor(){
        isRunning = false;
    }

    public void setLogListener(LogListener logListener) {
        this.listener = logListener;
    }

    public LogListener getLogListener() {
        return this.listener;
    }

    public Boolean getRunning() {
        return isRunning;
    }

    public void execute(String... cmds) {
        executor = Executors.newSingleThreadExecutor();
        ProcessBuilder processBuilder = new ProcessBuilder(cmds);
        processBuilder.redirectErrorStream(true);

        try {
            this.process = processBuilder.start();
            executor.execute(new LogReader(this.process.getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void terminate() {
        if (Objects.nonNull(process)) {
            try {
                process.waitFor();
                isRunning = false;
                process.destroy();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        executor.shutdown();
        if (Objects.nonNull(listener)) {
            listener.onMessage("Exit-Code: " + process.exitValue());
        }
    }

    public void terminateForcibly() {
        if (Objects.nonNull(process)) {
            isRunning = false;
            process.destroy();
        }
        executor.shutdown();
        if (Objects.nonNull(listener)) {
            listener.onMessage("Exit-Code: " + process.exitValue());
        }
    }

    public static void main(String[] args) throws InterruptedException {

        List<String> lines = new LinkedList<>();
        CommandExecutor commandExecutor = new CommandExecutor();

        commandExecutor.setLogListener(new LogListener() {
            @Override
            public void onMessage(String line) {
                lines.add(line);
            }
        });
        commandExecutor.execute("ping", "-c","5", "google.com");

        commandExecutor.terminate();

        for (String li: lines){
            System.out.println(li);
        }
    }

    public class LogReader implements Runnable {

        private BufferedReader reader;

        public LogReader(InputStream is) {
            this.reader = new BufferedReader(new InputStreamReader(is));
        }

        public void run() {
            try {
                String line = reader.readLine();
                while (line != null) {
                    if (Objects.nonNull(listener)) {
                        listener.onMessage(line);
                    }
                    line = reader.readLine();
                }
                reader.close();
            } catch (IOException e) {
            }
        }
    }

}
