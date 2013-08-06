package com.at.consumo;

/**
 * Created by at on 8/5/13.
 */
public class TagParser {
    private final String tagstart;
    private final String tagend;
    String tag;
    private final String st;
    private int from;

    public String getSt() {
        return st;
    }

    public int getFrom() {
        return from;
    }

    public TagParser(String tag, String st, int from) {
        this.tag = tag;
        this.st = st;
        this.from = from;

        tagstart = "<" + tag;
        tagend = "</" + tag + ">";
    }

    public String parse(){
        int indexFirst0 = st.indexOf(tagstart, from);
        int indexFirst = st.indexOf(">", indexFirst0);
        int indexFirstEnd = st.indexOf(tagend, indexFirst  );
        from = indexFirstEnd;
        //return new Tuple(indexFirstEnd,st.substring(indexFirst , indexFirstEnd));
        return st.substring(indexFirst +1 , indexFirstEnd);
    }


    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }
}
