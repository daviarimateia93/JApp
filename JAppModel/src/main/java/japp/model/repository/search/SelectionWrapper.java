package japp.model.repository.search;

import javax.persistence.criteria.Selection;

public class SelectionWrapper<T> {

    private final String alias;
    private final Selection<T> selection;

    public SelectionWrapper(final String alias, final Selection<T> selection) {
        this.alias = alias;
        this.selection = selection;
    }

    public String getAlias() {
        return alias;
    }

    public Selection<T> getSelection() {
        return selection;
    }
}
