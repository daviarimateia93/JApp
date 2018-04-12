package japp.web.view;

public class View {
    private final String layoutName;
    private final String name;

    public View(final String name) {
        this(null, name);
    }

    public View(final String layoutName, final String name) {
        this.layoutName = layoutName;
        this.name = name;
    }

    public String getLayoutName() {
        return layoutName;
    }

    public String getName() {
        return name;
    }
}
