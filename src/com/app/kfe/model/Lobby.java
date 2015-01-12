package com.app.kfe.model;

import com.app.kfe.model.messages.LobbyMessage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Lobby {

	/**
	 * Wiadomo�ci wys�ane w obr�bie tego lobby.
	 */
	private List<LobbyMessage> _messages;
	/**
	 * Komparator pozwalający na chronologiczne posortowanie wiadomości.
	 */
	private MessageChronicleComparator _messagesComparator;

	public Lobby() {
		_messages = new ArrayList<LobbyMessage>();
		_messagesComparator = new MessageChronicleComparator();
	}

	/**
	 * Metoda umo�liwia dodanie nowej wiadomo�ci do lobby.
	 * @param message Nowa wiadomo�� lobby
	 */
	public void addMessage(LobbyMessage message) {
		if(message != null) {
			_messages.add(message);
			Collections.sort(_messages,_messagesComparator);
		}
	}

	/**
	 * Metoda umo�liwia usuni�cie podanej wiadomo�ci z lobby.
	 * @param message Wiadomo��, kt�ra ma zosta� usuni�ta z lobby
	 */
	public void deleteMessage(LobbyMessage message) {
		if(message != null) {
			_messages.remove(message);
		}
	}

	/**
	 * Metoda zwraca list� wiadomo�ci wys�anych w obr�bie tego lobby.
	 * @return Lista wiadomo�ci
	 */
	public List<LobbyMessage> getMessages() {
		return _messages;
	}

	private class MessageChronicleComparator implements Comparator<LobbyMessage> {
		@Override
		public int compare(LobbyMessage arg0, LobbyMessage arg1) {
			return arg0.getSendDate().compareTo(arg1.getSendDate());
		}
	}
}