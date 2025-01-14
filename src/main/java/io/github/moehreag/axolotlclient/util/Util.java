package io.github.moehreag.axolotlclient.util;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.Address;
import net.minecraft.client.network.AllowedAddressResolver;
import net.minecraft.client.network.ServerAddress;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.NetworkState;
import net.minecraft.network.listener.ClientQueryPacketListener;
import net.minecraft.network.packet.c2s.handshake.HandshakeC2SPacket;
import net.minecraft.network.packet.c2s.query.QueryPingC2SPacket;
import net.minecraft.network.packet.c2s.query.QueryRequestC2SPacket;
import net.minecraft.network.packet.s2c.query.QueryPongS2CPacket;
import net.minecraft.network.packet.s2c.query.QueryResponseS2CPacket;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.scoreboard.ScoreboardPlayerScore;
import net.minecraft.scoreboard.Team;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;

public class Util {

	private static final ThreadPoolExecutor REALTIME_PINGER = new ScheduledThreadPoolExecutor(3, new ThreadFactoryBuilder().setNameFormat("Real Time Server Pinger #%d").setDaemon(true).build());
	public static int currentServerPing;

	public static String lastgame;
	public static String game;

	public static String getGame(){

		List<String> sidebar = getSidebar();

		if(sidebar.isEmpty()) game = "";
		else if (MinecraftClient.getInstance().getCurrentServerEntry() != null && MinecraftClient.getInstance().getCurrentServerEntry().address.toLowerCase().contains(sidebar.get(0).toLowerCase())){
			if ( sidebar.get(sidebar.size() -1).contains(MinecraftClient.getInstance().getCurrentServerEntry().address) || sidebar.get(sidebar.size()-1).contains("Playtime")){
				game = "In Lobby";
			}  else {
				if (sidebar.get(sidebar.size()-1).contains("--------")){
					game = "Playing Bridge Practice";
				} else {
					game = "Playing "+ sidebar.get(sidebar.size() -1);
				}
			}
		} else {
			game = "Playing "+ sidebar.get(0);
		}

		if(!Objects.equals(lastgame, game) && game.equals("")) game = lastgame;
		else lastgame = game;

		if (game==null){game="";}

		return game;
	}


	public static List<String> getSidebar() {
		List<String> lines = new ArrayList<>();
		MinecraftClient client = MinecraftClient.getInstance();
		if (client.world == null) return lines;

		Scoreboard scoreboard = client.world.getScoreboard();
		if (scoreboard == null) return lines;
		ScoreboardObjective sidebar = scoreboard.getObjectiveForSlot(1);
		if (sidebar == null) return lines;

		Collection<ScoreboardPlayerScore> scores = scoreboard.getAllPlayerScores(sidebar);
		List<ScoreboardPlayerScore> list = scores.stream()
			.filter(input -> input != null && input.getPlayerName() != null && !input.getPlayerName().startsWith("#"))
			.collect(Collectors.toList());

		if (list.size() > 15) {
			scores = Lists.newArrayList(Iterables.skip(list, scores.size() - 15));
		} else {
			scores = list;
		}

		for (ScoreboardPlayerScore score : scores) {
			Team team = scoreboard.getPlayerTeam(score.getPlayerName());
			if (team == null) return lines;
			String text = team.getPrefix().getString() + team.getSuffix().getString();
			if (text.trim().length() > 0)
				lines.add(text);
		}

		lines.add(sidebar.getDisplayName().getString());
		Collections.reverse(lines);

		return lines;
	}



	//Indicatia removed this feature...
	//We still need it :(
	public static void getRealTimeServerPing(ServerInfo server)
	{
		REALTIME_PINGER.submit(() ->
		{
			try
			{
				var address = ServerAddress.parse(server.address);
				var optional = AllowedAddressResolver.DEFAULT.resolve(address).map(Address::getInetSocketAddress);


				if (optional.isPresent())
				{

					ClientConnection manager = ClientConnection.connect( optional.get(), false);
					manager.setPacketListener(new ClientQueryPacketListener()
					{
						@Override
						public void onResponse(QueryResponseS2CPacket packet) {
							this.currentSystemTime = net.minecraft.util.Util.getMeasuringTimeMs();
							manager.send(new QueryPingC2SPacket(this.currentSystemTime));
						}

						@Override
						public void onPong(QueryPongS2CPacket packet) {
							var time = this.currentSystemTime;
							var latency = net.minecraft.util.Util.getMeasuringTimeMs();
							Util.currentServerPing = (int) (latency - time);
							manager.disconnect(new LiteralText(""));
						}

						private long currentSystemTime = 0L;

						@Override
						public void onDisconnected(Text reason) {

						}

						@Override
						public ClientConnection getConnection()
						{
							return manager;
						}
					});
					manager.send(new HandshakeC2SPacket(address.getAddress(), address.getPort(), NetworkState.STATUS));
					manager.send(new QueryRequestC2SPacket());
				}
			}
			catch (Exception ignored){}
		});
	}

}
