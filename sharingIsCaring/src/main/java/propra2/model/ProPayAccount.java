package propra2.model;

import lombok.Data;

import javax.persistence.ElementCollection;
import javax.persistence.Embeddable;
import javax.persistence.Lob;
import java.util.ArrayList;

@Data
@Embeddable
public class ProPayAccount {
    private String account;
    private double amount;

    @Lob
    private ArrayList<Reservation> reservations = new ArrayList<>();

    public void addReservation(Reservation reservation) {
        reservations.add(reservation);
    }

    public int getAvailableAmount(){
        int result= (int) amount;
        for(Reservation reservation : reservations){
            result -= reservation.getAmount();
        }
        return result;
    }
}
