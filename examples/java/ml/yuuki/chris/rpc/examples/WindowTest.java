package ml.yuuki.chris.rpc.examples;

import club.minnced.discord.rpc.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class WindowTest {

	private static final Path SETTINGS = FileSystems.getDefault().getPath("settings.json");
	private static String APP_ID="unset";

	public static void saveSettings(DiscordRichPresence presence){
		String jsonString = "{ \"appID\": \""+APP_ID+"\"," +
				"\"state\": \""+presence.state+"\","+
				"\"details\": \""+presence.details+"\","+
				"\"startTimestamp\": \""+presence.startTimestamp+"\","+
				"\"endTimestamp\": \""+presence.endTimestamp+"\","+
				"\"largeImageKey\": \""+presence.largeImageKey+"\","+
				"\"largeImageText\": \""+presence.largeImageText+"\","+
				"\"smallImageKey\": \""+presence.smallImageKey+"\","+
				"\"smallImageText\": \""+presence.smallImageText+"\","+
				"\"partyId\": \""+presence.partyId+"\","+
				"\"partySize\": \""+presence.partySize+"\","+
				"\"partyMax\": \""+presence.partyMax+"\","+
				"\"matchSecret\": \""+presence.matchSecret+"\","+
				"\"joinSecret\": \""+presence.joinSecret+"\","+
				"\"spectateSecret\": \""+presence.spectateSecret+"\","+
				"\"instance\": \""+presence.instance+"\""+
				" }";
		ArrayList<String> fileText = new ArrayList<>();
		fileText.add(jsonString);

		try {
			Files.write(SETTINGS, fileText, StandardCharsets.UTF_8);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static DiscordRichPresence loadSettings(){
		DiscordRichPresence presence = new DiscordRichPresence();
		try {
			List<String> content = Files.readAllLines(SETTINGS);
			String[] parsed = content.get(0).split("\"");
			ArrayList<String> values = new ArrayList();
			for(int i=0; true; i++){
				if(i%4==3){
					System.out.println(parsed[i]);
					if(parsed[i]=="null"){
						values.add("");
					}else {
						values.add(parsed[i]);
					}
				}
				if(i>=parsed.length){
					break;
				}
			}
			APP_ID=values.get(0);
			presence.state=values.get(1);
			presence.details=values.get(2);
			presence.startTimestamp=Long.parseLong(values.get(3));
			presence.endTimestamp=Long.parseLong(values.get(4));
			presence.largeImageKey=values.get(5);
			presence.largeImageText=values.get(6);
			presence.smallImageKey=values.get(7);
			presence.smallImageText=values.get(8);
			presence.partyId=values.get(9);
			presence.partySize=Integer.parseInt(values.get(10));
			presence.partyMax=Integer.parseInt(values.get(11));
			//presence.matchSecret=values.get(12);
			//presence.joinSecret=values.get(13);
			//presence.spectateSecret=values.get(14);
			presence.instance=Byte.parseByte(values.get(15));
		} catch (IOException e) {
			e.printStackTrace();
		}

		return presence;
	}

	public static void main(String args[]) {
		DiscordRPC lib = DiscordRPC.INSTANCE;
		DiscordRichPresence presence = loadSettings();//new DiscordRichPresence();
		DiscordEventHandlers handlers = new DiscordEventHandlers();
		handlers.ready = (user) -> System.out.println("Ready!");



		presence.startTimestamp = System.currentTimeMillis() / 1000; // epoch second
		//presence.endTimestamp   = presence.startTimestamp + 20;

		presence.partySize = 0;
		presence.partyMax  = 0;
		//lib.Discord_UpdatePresence(presence);
		
		Thread t = new Thread(() -> {
			while (!Thread.currentThread().isInterrupted()) {
				lib.Discord_RunCallbacks();
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					lib.Discord_Shutdown();
					break;
				}
			}
		}, "RPC-Callback-Handler");

		
		JFrame frame = new JFrame("Java-Discord RPC");
		GridLayout frameLayout = new GridLayout(2, 1);
		frame.setLayout(frameLayout);

		if (args.length == 0) {
			//JDialog dialog = new JDialog();
			Object[] possibilities = null;
			String s = (String)JOptionPane.showInputDialog(
					frame,
					"Enter your app ID:",
					"App ID needed",
					JOptionPane.PLAIN_MESSAGE,
					null,
					possibilities,
					APP_ID);

//If a string was returned, say so.
			if ((s != null) && (s.length() > 0)) {
				APP_ID = s;
				lib.Discord_Initialize(APP_ID, handlers, true, "");
			}
			//System.err.println("You must specify an application ID in the arguments!");
			//System.exit(-1);
		}else{
			String applicationId = args.length < 1 ? "" : args[0];
			String steamId       = args.length < 2 ? "" : args[1];
			lib.Discord_Initialize(applicationId, handlers, true, steamId);
		}

		t.start();

		JPanel top = new JPanel();
		GridLayout topLayout = new GridLayout(8, 2);
		top.setLayout(topLayout);
		
		JPanel bottom = new JPanel();
		GridLayout botLayout = new GridLayout(8, 1);
		bottom.setLayout(botLayout);
		
		
		// Details


		JLabel detailsLabel = new JLabel("Details");
		detailsLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		JTextField detailsText = new JTextField(presence.details);

		JLabel stateLabel = new JLabel("State");
		stateLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		JTextField stateText = new JTextField(presence.state);

		JLabel smallImageKeyLabel = new JLabel("Small image key");
		smallImageKeyLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		JTextField smallImageKeyText = new JTextField(presence.smallImageKey);

		JLabel smallImageTextLabel = new JLabel("Small image text");
		smallImageTextLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		JTextField smallImageTextText = new JTextField(presence.smallImageText);

		JLabel largeImageKeyLabel = new JLabel("Large image key");
		largeImageKeyLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		JTextField largeImageKeyText = new JTextField(presence.largeImageKey);

		JLabel largeImageTextLabel = new JLabel("Large image text");
		largeImageTextLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		JTextField largeImageTextText = new JTextField(presence.largeImageText);

		JLabel partyLabel = new JLabel("Party size");
		partyLabel.setHorizontalAlignment(SwingConstants.CENTER);
		JTextField partySizeText = new JTextField(String.valueOf(presence.partySize));
		JLabel partyOfLabel = new JLabel("out of");
		partyOfLabel.setHorizontalAlignment(SwingConstants.CENTER);
		JTextField partyMaxText = new JTextField(String.valueOf(presence.partyMax));

		JCheckBox displayTime = new JCheckBox("Display time spent");
		
		JButton submit = new JButton("Update Presence");
		submit.addActionListener(e -> {
			if(displayTime.isSelected()){
				presence.startTimestamp = System.currentTimeMillis() / 1000;
			}else {
				presence.startTimestamp = 0;
			}

			presence.details = detailsText.getText();
			presence.state   = stateText.getText();
			presence.smallImageKey = smallImageKeyText.getText();
			presence.smallImageText = smallImageTextText.getText();
			presence.largeImageKey = largeImageKeyText.getText();
			presence.largeImageText = largeImageTextText.getText();


			try {
				presence.partySize = Integer.parseInt(partySizeText.getText());
			} catch (Exception err) {
				presence.partySize = 0;
			}
			try {
				presence.partyMax  = Integer.parseInt(partyMaxText.getText());
			} catch (Exception err) {
				presence.partySize = 0;
			} // if text isn't a number, ignore it
			
			lib.Discord_UpdatePresence(presence);
			System.out.println("App id: "+APP_ID);
			saveSettings(presence);
		});
		
		top.add(detailsLabel);
		top.add(detailsText);
		top.add(stateLabel);
		top.add(stateText);

		top.add(largeImageTextLabel);
		top.add(largeImageTextText);
		top.add(largeImageKeyLabel);
		top.add(largeImageKeyText);

		top.add(smallImageTextLabel);
		top.add(smallImageTextText);
		top.add(smallImageKeyLabel);
		top.add(smallImageKeyText);

		top.add(displayTime);

		bottom.add(partyLabel);
		bottom.add(partySizeText);
		bottom.add(partyOfLabel);
		bottom.add(partyMaxText);
		bottom.add(new JPanel());
		bottom.add(submit);
		bottom.add(new JPanel()); // dummy components to center the update button
		bottom.setPreferredSize(new Dimension(500, 20));


		frame.add(top);
		frame.add(bottom);
		
		frame.addWindowListener(new WindowAdapter() {
			
			@Override
			public void windowClosing(WindowEvent e) {
				e.getWindow().setVisible(false);
				t.interrupt();
			}
		});
		
		frame.pack();
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
		
	}

}
