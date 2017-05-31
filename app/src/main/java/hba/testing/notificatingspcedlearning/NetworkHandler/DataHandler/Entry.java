package hba.testing.notificatingspcedlearning.NetworkHandler.DataHandler;

/**
 * Created by TranKim on 3/5/2017.
 */
public class Entry {
    private String source;
    private String name;
    private String content;
    private String hint;
    private String group;//ex:phrasal verb, idiom...

    public String getName() {
        return name;
    }

    public String getContent() {
        return content;
    }

    public String getHint() {
        return hint;
    }

    public String getGroup() {
        return group;
    }

    public String getSource() {
        return source;
    }
    public void setName(String name) {
        this.name=name;
    }

    public void setContent(String content) {
        this.content=content;
    }

    public void setHint(String hint) {
        this.hint=hint;
    }

    public void setGroup(String group) {
        this.group=group;
    }

    public void setSource(String source) {
        this.source=source;
    }

    public Entry(String source, String name, String content, String hint, String group) {
        this.source = source;
        this.name = name;
        this.content = content;
        this.hint = hint;
        this.group = group;
    }
}
