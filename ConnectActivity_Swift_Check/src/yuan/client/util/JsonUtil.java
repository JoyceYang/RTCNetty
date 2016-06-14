package yuan.client.util;

import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;

/**
 * ����һ����json�ַ���ת���ɸ����������ݽṹ�Ĺ�����
 * 
 * @author Jack Yuan
 *
 */
public class JsonUtil {

	/**
	 * ��jsonת���������Map����
	 * 
	 * @param <T>
	 *            key�����ͣ�����
	 * @param <M>
	 *            value�����ͣ�����
	 * @param jsonString
	 * @return
	 */
	public static <T, M> Map<T, M> toCommonMap(String jsonString) {
		Map<T, M> commonMap = JSON.parseObject(jsonString,
				new TypeReference<Map<T, M>>() {
				});
		return commonMap;
	}

	public static <T, M, N> Map<T, List<Map<M, N>>> toListMap(String jsonString) {
		Map<T, List<Map<M, N>>> listmap = JSON.parseObject(jsonString,
				new TypeReference<Map<T, List<Map<M, N>>>>() {
				});
		return listmap;
	}
	
	

//	@Test
//	/**
//	 * ��Ԫ���Ժ�����������Ӧ��ת���Ƿ���ȷ
//	 */
//	public void testUtil() {
//		String sdpString = "{\"c89485ad\":\"offersdpoffersdp\"}";
//		String candidateString = "{\"c89485ad\":[{\"candidate\":\"candidate\",\"id\":\"1\",\"label\":\"1\"}]}";
//		Map<String,String> sdpmap=toCommonMap(sdpString);
//		System.out.println(sdpmap.toString());
//		Map<String,List<Map<String,String>>> candidatesmap=toListMap(candidateString);
//		System.out.println(candidatesmap);
//	}

}
