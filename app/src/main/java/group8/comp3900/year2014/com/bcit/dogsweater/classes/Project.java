package group8.comp3900.year2014.com.bcit.dogsweater.classes;

import android.net.Uri;

/**
 * Created by Chris on 2014-10-15.
 */
public class Project {
    //for databasing
    private long id;

    /**
     * dimensions keys of the dimensions object returned through getDimensions
     */
    public static final String KEY_GAUGE = "GAUGE";
    public static final String KEY_STS_NECK = "AA";
    public static final String KEY_STS_CHEST = "BB";
    public static final String KEY_STS_CENTRE_BACK = "FF";
    public static final String KEY_STS_CHEST_AREA = "GG";
    public static final String KEY_STS_NECK_TO_CHEST = "HH";
    public static final String KEY_STS_FIRST_LEGHOLE = "II";
    public static final String KEY_STS_STOMACH = "JJ";
    public static final String KEY_STS_BACK_FLAP = "KK";

    /**
     * array of mandatory dimensions keys that should be saved in the Dimensions
     * object returned by getDimensions
     */

    private String name;
    private float percentDone;
    private int rowCounter;
    private int curSection;
    private Profile profile;
    private Style style;

    private Uri imageURI = null;

    /** thickness of the yarn used for this project */
    private double gauge;

    /**
     * Author: Chris Klassen
     *
     * Constructor for a project object.
     */
    public Project(Profile p, Style s) {
        this( "Temp", 0, 0, p, s, 0 );
    }

    /**
     * Author Rhea Lauzon
     * @param p : profile object
     *          when the profile is known but style isnt determined yet
     */
    public Project(Profile p) {
        //TODO: FIX LATER TO PROPER DEFAULT VAL
        this( p, new Style("Style 1", 1) );
    }

    /**
     * Author: Chris Klassen
     *
     * Database constructor for a project object.
     */
    public Project(String n, float pd, int r, Profile p, Style s, int cs) {
        setName( n );
        setPercentDone( pd );
        rowCounter = r;
        profile = p;
        setStyle( s );
        curSection = cs;
    }

    public Project(String n, float pd, int r, Profile p, Style s, Uri imageURI, int cs) {
        this( n, pd, r, p, s, cs );
        setImageURI( imageURI );
    }

    public Project(String n, float pd, int r, Profile p, Style s, String imageURI, int cs) {
        this( n, pd, r, p, s, cs );
        setImageURI( imageURI );
    }

    public void setName(String n) {
        name = n;
    }

    public String getName() {
        return name;
    }

    public void setSection(int s) {
        curSection = s;
    }

    public int getSection() {
        return curSection;
    }

    public void setPercentDone(float p) {
        percentDone = p;
    }

    public void setGauge(double newGauge) {
        gauge = newGauge;
    }

    public void addPercent(float p) {
        percentDone += p;
    }

    public void incrementRowCounter() {
        rowCounter++;
    }

    public void decrementRowCounter() {
        if ( rowCounter > 0 ) {
            rowCounter--;
        }
    }

    public void resetRowCounter() {
        rowCounter = 0;
    }

    public int getRowCounter() {
        return rowCounter;
    }

    public float getPercentDone() {
        return percentDone;
    }

    public Profile getProfile() {
        return profile;
    }

    public Style getStyle() {
        return style;
    }

    public Dimensions getDimensions() {
        Dimensions dimensions = new Dimensions();
        dimensions.setDimension(KEY_GAUGE, getGauge());
        dimensions.setDimension(K);
    }

    public long getId() {

        return id;
    }

    public double getGauge() {
        return gauge;
    }

    public void setId( long id ) {

        this.id = id;
    }

    public void setStyle(Style s){
        this.style = s;
    }

    public void setImageURI( String newImageURI ) {

        if( newImageURI == null || newImageURI.equalsIgnoreCase( "null" ) ) {

            setImageURI( (Uri) null );
        }
        else {

            setImageURI( Uri.parse( newImageURI ) );
        }

    }

    public void setImageURI( Uri newImageURI ) {

        this.imageURI = newImageURI;
    }

    public Uri getImageURI() {

        return imageURI;
    }

    @Override
    public String toString()
    {
        return "Project[id:"      + id
                + ",name:"        + name
                + ",imageURI:"    + imageURI
                + ",percentDone:" + percentDone
                + ",rowCounter:"  + rowCounter
                + ",curSection:"  + curSection
                + ",profile:"     + profile
                + ",style:"       + style  + "]";
    }
}
