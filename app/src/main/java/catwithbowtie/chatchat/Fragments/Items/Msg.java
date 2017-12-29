package catwithbowtie.chatchat.Fragments.Items;

public class Msg extends Obj {
    public String text;
    public String from;
    public Msg(String text, String from) {
        this.text = text;
        this.from = from;
    }
}
