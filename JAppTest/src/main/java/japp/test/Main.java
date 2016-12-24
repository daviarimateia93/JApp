package japp.test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import japp.util.FormDataHelper;
import japp.util.JsonHelper;
import japp.util.Proxy;
import japp.util.StringHelper;

public class Main {
	public static void test(final String string) {
		if (StringHelper.isNullOrBlank(string) || string.length() > 45) {
			System.out.println("USER_ENTITY_NICKNAME_SHOULD_CONTAIN_UP_TO_45_CHARACTERS");
		}
	}
	
	@SuppressWarnings("unused")
	public static void main(final String[] args) {
		String pattern = "/rest/{anything}".replaceAll("\\{(.+?)\\}", "([a-zA-Z0-9\\\\\\%\\\\\\-\\\\\\.\\\\\\_\\\\\\~\\\\\\:\\\\\\/\\\\\\?\\\\\\#\\\\\\[\\\\\\]\\\\\\@\\\\\\!\\\\\\$\\\\\\&\\\\\\'\\\\\\(\\\\\\)\\\\\\*\\\\\\+\\\\\\,\\\\\\;\\\\\\=]*?)");
		Pattern.compile("^(" + pattern + ")$", Pattern.CASE_INSENSITIVE);
		
		test(null);
		Map<String, Object> map0 = FormDataHelper.parse("nome=davi&idade=23&enderecos[]=rua1&enderecos[]=rua2&telefone[0].ddd=11&telefone[0].numero=123123&telefone[1].ddd=12&telefone[1].numero=456456");
		Map<String, Object> map1 = FormDataHelper.parse("telefone[0].composicao.numbers[]=456456&pessoa.nome.primeiro=davi");
		System.out.println(map1);
		System.out.println(JsonHelper.toString(map1));
		
		SampleClass sc = Proxy.intercept(SampleClass.class, new Class<?>[] { String.class }, "davi");
		sc.hello();
		
		SampleJob sampleJob = new SampleJob();
		sampleJob.run();
		sampleJob.run();
		
		EntityB entityB1 = new EntityB();
		entityB1.setValue("valor 1");
		
		EntityB entityB2 = new EntityB();
		entityB2.setValue("valor 2");
		
		List<EntityB> entitiesB = new ArrayList<>();
		entitiesB.add(entityB1);
		entitiesB.add(entityB2);
		
		EntityA entityA = new EntityA();
		entityA.setA(1);
		entityA.setB("B");
		entityA.setEntitiesB(entitiesB);
		
		EntityA mergedEntityA = new EntityA();
		
		System.out.println(JsonHelper.toString(mergedEntityA));
		
		// new OCRTester();
		
		new Tess4jTester();
	}
}
