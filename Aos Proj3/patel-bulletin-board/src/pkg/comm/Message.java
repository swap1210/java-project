package pkg.comm;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

import pkg.comm.Basic.Topic;

public class Message implements Serializable {
    private Topic topic;
    private String pub_name;
    private String text;
    private LocalDateTime timestamp;

    public Message(Topic topic, String pub_name, String text) {
        this.topic = topic;
        this.pub_name = pub_name;
        this.text = text;
        this.timestamp = LocalDateTime.now();
    }

    public Topic getTopic() {
        return topic;
    }

    @Override
    public String toString() {
        return String.format("%s-[%s] %s: %s", DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT)
                .format(this.timestamp), this.topic, this.pub_name, this.text);
    }
}
