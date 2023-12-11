package io.github.chronosxyz.JGUN.examples.chat;

import io.github.chronosx88.JGUN.api.Gun;
import io.github.chronosx88.JGUN.api.graph.ArrayBuilder;
import io.github.chronosx88.JGUN.api.graph.NodeBuilder;
import io.github.chronosx88.JGUN.models.graph.Node;
import io.github.chronosx88.JGUN.models.graph.NodeValue;
import io.github.chronosx88.JGUN.models.graph.values.ArrayValue;
import io.github.chronosx88.JGUN.models.graph.values.NodeLinkValue;
import io.github.chronosx88.JGUN.models.graph.values.StringValue;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutionException;

public class FXMLController implements Initializable {
    @FXML
    Button sendButton;

    @FXML
    TextArea chatBox;

    @FXML
    TextArea msgEditbox;

    @FXML
    TextField nicknameEditbox;

    private Gun gun;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        Platform.runLater(() -> {
            gun.get("chat").map((k, v) -> {
                if (!k.equals("messages")) return;
                ArrayValue val = (ArrayValue) v;
                val.getValue().forEach((e) -> {
                    if (e.getValueType() == NodeValue.ValueType.LINK) {
                        Node obj;
                        try {
                            obj = gun.get(((NodeLinkValue) e).getLink()).once().get().getData();
                        } catch (InterruptedException | ExecutionException ex) {
                            throw new RuntimeException(ex);
                        }
                        StringValue fromVal = (StringValue) obj.getValues().get("from");
                        StringValue msgVal = (StringValue) obj.getValues().get("text");
                        StringValue dateVal = (StringValue) obj.getValues().get("date");
                        chatBox.appendText(String.format("[%s] <%s> %s\n", dateVal.getValue(), fromVal.getValue(), msgVal.getValue()));
                    }
                });
            });
        });

        sendButton.setOnAction((e) -> {
            try {
                gun.get("chat")
                        .put(new NodeBuilder().add("messages", new ArrayBuilder()
                                .add(new NodeBuilder()
                                        .add("date", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                                                .format(Calendar.getInstance().getTime()))
                                        .add("from", nicknameEditbox.getText())
                                        .add("text", msgEditbox.getText()))).build())
                        .get();
            } catch (InterruptedException | ExecutionException ex) {
                throw new RuntimeException(ex);
            }
            msgEditbox.clear();
        });
    }

    public void setGunInstance(Gun gun) {
        this.gun = gun;
    }
}