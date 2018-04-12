package japp.stp.command;

public class Command {

    private String name;
    private String[] values;

    public Command(final String name, final String... values) {
        this.name = name;
        this.values = values;
    }

    public String getName() {
        return name;
    }

    public String[] getValues() {
        return values;
    }

    @Override
    public String toString() {
        final StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append(name + ";");

        if (values != null) {
            for (final String value : values) {
                stringBuilder.append(value + ";");
            }
        }

        return stringBuilder.toString();
    }
}
