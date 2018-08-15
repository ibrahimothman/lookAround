package com.example.ibrakarim.lookaround.model;

public class Photos {

    private String photo_reference;

    private String height;

    private String width;

    public String getPhoto_reference ()
    {
        return photo_reference;
    }

    public void setPhoto_reference (String photo_reference)
    {
        this.photo_reference = photo_reference;
    }

    public String getHeight ()
    {
        return height;
    }

    public void setHeight (String height)
    {
        this.height = height;
    }

    public String getWidth ()
    {
        return width;
    }

    public void setWidth (String width)
    {
        this.width = width;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [photo_reference = "+photo_reference+", height = "+height+", width = "+width+"]";
    }
}
