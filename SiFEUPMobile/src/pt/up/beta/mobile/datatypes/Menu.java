package pt.up.beta.mobile.datatypes;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Class Menu. Save the information of menu.
 * 
 * @author Ângela Igreja
 * 
 */
public class Menu implements Parcelable {
    private String state;
    private String date;
    private Dish[] dishes;

    public Menu() {
    }
    
    
    public String getState() {
        return state;
    }
    public void setState(String state) {
        this.state = state;
    }
    public String getDate() {
        return date;
    }
    public void setDate(String date) {
        this.date = date;
    }
    public Dish[] getDishes() {
        return dishes;
    }
    public void setDishes(Dish[] dishes) {
        this.dishes = dishes;
    }
    @Override
    public int describeContents() {
        return 0;
    }
    @Override
    public void writeToParcel(Parcel dest, int flags) {
    	dest.writeInt(state!=null?1:0);
        dest.writeString(state);
    	dest.writeInt(date!=null?1:0);
        dest.writeString(date);
    	dest.writeInt(dishes!=null?1:0);
        dest.writeValue(dishes);
    }
    
    public static final Parcelable.Creator<Menu> CREATOR = new Parcelable.Creator<Menu>() {
        public Menu createFromParcel(Parcel in) {
            return new Menu(in);
        }

        public Menu[] newArray(int size) {
            return new Menu[size];
        }
    };

    private Menu(Parcel in) {
    	if ( in.readInt() == 1 )
    		state = in.readString();
    	if ( in.readInt() == 1 )
    		date = in.readString();
    	if ( in.readInt() == 1 )
    		dishes = (Dish[]) in.readValue(Dish.class.getClassLoader());
    }
}