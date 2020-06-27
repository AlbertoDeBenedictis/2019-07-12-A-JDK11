package it.polito.tdp.food.model;

public class Event implements Comparable<Event> {

	public enum EventType {
		INIZIO_PREPARAZIONE, // viene assegnato un cibo a una stazione
		FINE_PREPARAZIONE, // la stazione K ha completato la preparazione del cibo
	}

	// Caratteristiche dell'evento

	private EventType type;
	private Double time; // tempo in minuti
	private Food food; // cosa preparo
	private Stazione stazione; // dove lo preparo

	public Event(Double time, EventType type, Food food, Stazione stazione) {
		super();

		this.time = time;
		this.type = type;
		this.food = food;
		this.stazione = stazione;
	}

	public EventType getType() {
		return type;
	}

	public void setType(EventType type) {
		this.type = type;
	}

	public Double getTime() {
		return time;
	}

	public Food getFood() {
		return food;
	}

	public Stazione getStazione() {
		return stazione;
	}

	@Override
	public int compareTo(Event o) {
		// TODO Auto-generated method stub
		return this.time.compareTo(o.time);
	}

}

// Inizio preparazione, scelgo il prossimo cibo, calcolo la durata, schedulo l'evento fine

// Fine preparazione, rischedulo evento di inizio allo stesso istante, ricordando il cibo temrinato