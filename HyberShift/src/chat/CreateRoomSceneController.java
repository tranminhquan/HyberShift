package chat;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import application.Main;
import chatsocket.ChatSocket;

import com.github.nkzawa.emitter.Emitter.Listener;
import com.github.nkzawa.socketio.client.Socket;
import com.jfoenix.controls.*;

import dataobject.Room;
import dataobject.UserInfo;

public class CreateRoomSceneController {
	@FXML
	JFXTextField tfRoomName;
	@FXML
	JFXTextField tfMembers;
	@FXML
	JFXButton btnConfirm;
	
	Socket socket;
	UserInfo userInfo;
	
	public CreateRoomSceneController(){
		userInfo = UserInfo.getInstance();
		socket = ChatSocket.getInstance().getSocket();
		
		socket.on("create_room_result", new Listener() {
			
			@Override
			public void call(Object... args) {
				JSONObject jsoninfo = (JSONObject) args[0];
				JSONArray invalid;
				try {
					invalid = jsoninfo.getJSONArray("invalid");
					if (invalid.length() == 0){
						Platform.runLater(new Runnable() {
							@Override
							public void run() {
								new Alert(AlertType.INFORMATION, "Create room successfully!").show();
								Main.showMainChatScene();
							}
						});
					}
					else{
						Platform.runLater(new Runnable() {
							@Override
							public void run() {
								new Alert(AlertType.INFORMATION, "Create room successfully, emails " + invalid + " is not valid.").show();
							}
						});	
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		
	}
	
	@FXML
	public void onActionBtnConfirmClick(){
		System.out.println("btnConfirm click");
		
		//Check valid
		if (!isValid()){
			new Alert(AlertType.INFORMATION, "Please fill all fields").show();
			return;
		}
		
		//Get text from tfRoomName and tfMembers
		String roomName;
		String[] members;
		//--tfRoomName
		roomName = tfRoomName.getText().toString();
		
		//--tfMembers
		members = tfMembers.getText().replaceAll("\\s+","").split(",");
		
		
		try {
			JSONObject roomjson = new JSONObject();
			
			roomjson.put("room_name", roomName);
			roomjson.put("creator_name", userInfo.getFullName());
			roomjson.put("creator_email", userInfo.getEmail());
			roomjson.put("members", members);
			
			//Emit to server
			socket.emit("create_room", roomjson);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private boolean isValid(){
		if (tfRoomName.getText().trim().length() == 0)
			return false;
		if (tfMembers.getText().trim().length() == 0)
			return false;
		
		return true;
	}
	
	
}