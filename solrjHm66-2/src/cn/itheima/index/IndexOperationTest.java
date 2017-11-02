package cn.itheima.index;

import java.util.List;
import java.util.Map;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrQuery.ORDER;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;
import org.junit.Before;
import org.junit.Test;

public class IndexOperationTest {
	
	private HttpSolrServer solrServer;
	
	@Before
	public void init() throws Exception {
		// Solr服务的URL
		String solrServerUrl = "http://localhost:8081/solr/collection1";
		solrServer = new HttpSolrServer(solrServerUrl);
	}
	
	// 使用动态域来创建文档对象和索引
	@Test
	public void createIndex() throws Exception {
		
		// 创建一个Document对象
		SolrInputDocument solrInputDocument = new SolrInputDocument();
		solrInputDocument.addField("id", "zjl001");
		solrInputDocument.addField("zhoujielun_s", "周杰伦");
		
		solrServer.add(solrInputDocument);
		
		solrServer.commit();
	}
	
	// 使用solrj实现复杂的条件查询
	@Test
	public void searchTest() throws Exception {
		
		// 设置查询条件
		SolrQuery query = this.setQueryConditions();
		// 执行查询
		QueryResponse response = solrServer.query(query);
		// 处理查询结果
		this.dealSearchResults(response);
		
	}
	
	// 设置查询条件
	private SolrQuery setQueryConditions() throws Exception {
		// 创建查询对象
		SolrQuery query = new SolrQuery();
		
		// 设置查询关键字
		query.setQuery("台灯");
		
		// 设置过滤条件
		//query.setFilterQueries("product_catalog_name:雅致灯饰", "product_price:[30 TO 40]");
		query.setFilterQueries("product_catalog_name:雅致灯饰");
		query.setFilterQueries("product_price:[30 TO 40]");
		
		// 设置排序条件
		query.setSort("product_price", ORDER.asc);
		
		// 设置分页条件
		query.setStart(0);
		query.setRows(3);
		
		// 设置显示的域名称
		query.setFields("id", "product_name", "product_price");
		
		// 设置默认搜索域
		query.set("df", "product_keywords");
		
		// 设置高亮信息(是否高亮显示, 高亮显示的域名, 高亮显示内容的前缀和后缀)
		query.setHighlight(true);
		query.addHighlightField("product_name");
		query.setHighlightSimplePre("<font color=\"red\">");
		query.setHighlightSimplePost("</font>");
		return query;
	}
	
	// 处理我们的返回值
	private void dealSearchResults(QueryResponse response) throws Exception {
		// 取得查询结果集
		SolrDocumentList results = response.getResults();
		
		// 打印查询结果总件数
		System.out.println("查询结果总件数:" + results.getNumFound());
		
		// 遍历结果集, 进行打印输出
		for (SolrDocument doc : results) {
			
			// 取得文档id
			String docId = String.valueOf(doc.get("id"));
			
			// 取得原始的商品名称
			String productName = String.valueOf(doc.get("product_name"));
			
			// 高亮显示处理
			Map<String, Map<String, List<String>>> highlighting = response.getHighlighting();
			if (highlighting != null) {
				List<String> list = highlighting.get(docId).get("product_name");
				if (list != null && list.size() > 0) {
					productName = list.get(0);
				}
			}
			
			// 打印输出
			System.out.println("============================");
			System.out.println("商品ID:" + docId);
			System.out.println("商品名称:" + productName);
			System.out.println("商品价格:" + doc.get("product_price"));
			
		}
		
	}
	
	
	
	
	
	
	
	
	
	

}
