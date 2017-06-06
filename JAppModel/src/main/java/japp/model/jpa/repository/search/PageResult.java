package japp.model.jpa.repository.search;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class PageResult<T> implements Serializable {
	
	private static final long serialVersionUID = -3784448232921351412L;
	
	private final List<T> data = new ArrayList<>();
	private final Long total;
	private final Long totalFiltered;
	private final Integer page;
	private final Integer pageSize;
	private final Integer pageTotal;
	
	public PageResult(final List<T> data, final long total, final long totalFiltered, final int firstResult, final long maxResults) {
		if (data != null) {
			this.data.addAll(data);
		}
		
		this.total = total;
		this.totalFiltered = totalFiltered;
		this.pageSize = this.data.isEmpty() ? 1 : this.data.size();
		this.pageTotal = this.data.isEmpty() ? 1 : (int) Math.ceil((double) totalFiltered / maxResults);
		this.page = this.data.isEmpty() ? 1 : this.pageTotal - (int) Math.ceil((double) (totalFiltered - firstResult) / maxResults) + 1;
	}
	
	public List<T> getData() {
		return data;
	}
	
	public Long getTotal() {
		return total;
	}
	
	public Long getTotalFiltered() {
		return totalFiltered;
	}
	
	public Integer getPage() {
		return page;
	}
	
	public Integer getPageSize() {
		return pageSize;
	}
	
	public Integer getPageTotal() {
		return pageTotal;
	}
}
