import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.json.*;

import java.util.Scanner;

public class App {
    public static void main(String[] args) {
        String subTopic = "/iot/sub";
        String pubTopic = "/iot/sub";
        int qos = 2;
        String broker = "tcp://broker.hivemq.com:1883";
        String clientId = "JAVA_APP";
        MemoryPersistence persistence = new MemoryPersistence();
        try {
            MqttClient client = new MqttClient(broker, clientId, persistence);
            // MQTT connection option
            MqttConnectOptions connOpts = new MqttConnectOptions();
            // retain session
            connOpts.setCleanSession(true);
            // set callback
            client.setCallback(new OnMessageCallback());
            // establish a connection
            System.out.println("Connecting to broker: " + broker);
            client.connect(connOpts);
            System.out.println("Connected!");
            // Subscribe
            client.subscribe(subTopic);

            // Required parameters for message publishing
            while (true) {
                System.out.println("Publishing message: ");
                Scanner scanner = new Scanner(System.in);
                String content = scanner.nextLine();
                if (content.isEmpty()) break;
                String payloadString = "{\"id\":" + clientId + ",\"payload\":" + content + "}";
                JSONObject payload = new JSONObject(payloadString);
                MqttMessage message = new MqttMessage(payload.toString().getBytes());
                message.setQos(qos);
                client.publish(pubTopic, message);
                System.out.println("Message published");
            }
            client.disconnect();
            System.out.println("Disconnected");
            client.close();
            System.exit(0);
        } catch (MqttException me) {
            me.printStackTrace();
        }
    }
}
