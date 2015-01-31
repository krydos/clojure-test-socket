(ns clj-server.core
  (:gen-class)
  (:require [clojure.java.io :as io]
            [clj-server.message_handler :as msg_h]
            [clj-server.connections_list :as conn_l])
  (:import [java.net ServerSocket]))

(def ^:const port 8888)

(defn sock-receive
  "receive message from socket"
  [socket]
  (.readLine (io/reader socket)))

(defn sock-send
  "send message to socket"
  [socket msg]
  (let [writer (io/writer socket)]
    (.write writer msg)
    (.flush writer)))

(defn get-into-message-loop
  "message loop.
  trying to receive message from socket, process it and send answer back to socket"
  [sock]
  (let [msg (sock-receive sock)]
    (sock-send sock (msg_h/process msg))))
  (recur sock))

(defn accept-and-process
  "accepting new connection and run message handler in new thread"
  [l-socket]
  (let [socket (.accept l-socket)]
    (future (get-into-message-loop socket)))
  (recur l-socket))

(defn -main
  "run server"
  []
  (let [l-socket (ServerSocket. port)]
    (future (accept-and-process l-socket))
    (println "Server started...")))

(defn start-server
  "using this function to run server in the background thread"
  []
  (.start (Thread. -main)))
