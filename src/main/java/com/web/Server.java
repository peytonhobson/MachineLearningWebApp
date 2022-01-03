package com.web;

import lombok.SneakyThrows;
import weka.classifiers.Classifier;
import weka.classifiers.bayes.NaiveBayes;
import weka.core.converters.ConverterUtils;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Formatter;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static weka.classifiers.evaluation.Evaluation.evaluateModel;

public class Server {

    private ServerSocket serverSocket;

    private ExecutorService runService;

    public Server() {

        runService = Executors.newFixedThreadPool(1);

        try {
            serverSocket = new ServerSocket(23595,2);
        }
        catch(IOException ioException) {
            ioException.printStackTrace();
            System.exit(1);
        }
    }

    public void execute() {
        try {
            User user = new User(serverSocket.accept());
            runService.execute(user);
        }
        catch(IOException ioException) {
            ioException.printStackTrace();
            System.exit(1);
        }
    }

    private class User implements Runnable {

        /**
         * connection to client
         */
        private Socket connection; // connection to client

        /**
         * Input stream from client
         */
        private Scanner input; // input from client

        /**
         * Output to client
         */
        private Formatter output; // output to client

        /**
         * Constructor that initializes I/O
         * @param socket  socket connection to client
         */
        public User(Socket socket)
        {
            connection = socket; // store socket for client

            try // obtain streams from Socket
            {
                input = new Scanner(connection.getInputStream());
                output = new Formatter(connection.getOutputStream());
            }
            catch (IOException ioException)
            {
                ioException.printStackTrace();
                System.exit(1);
            }
        }


        public void run() {

            String inputString;

            while(!connection.isClosed()) {
                while(!input.hasNext()) {

                }

                if(input.hasNext()) {
                    // TODO: may have to debug this to parse for json
                    inputString = input.next();
                }

                StringBuffer outBuff = new StringBuffer();

                try {
                    ConverterUtils.DataSource source = new ConverterUtils.DataSource("https://storm.cis.fordham.edu/~gweiss/data-mining/weka-data/weather.arff");
                }
                catch(Exception exception) {
                    exception.printStackTrace();
                    System.exit(1);
                }

                // TODO: Add way to differentiate between datasets for classIndex

                String[] args = new String[2];
                args[0] = "-t";
                args[1] = "https://storm.cis.fordham.edu/~gweiss/data-mining/weka-data/weather.arff";

                Classifier classifier = new NaiveBayes();

                System.out.println(evaluateModel(classifier, args));

            }



        }







    }


}
