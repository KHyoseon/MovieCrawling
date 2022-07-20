package naver_movie;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class MainApp {

	static class MovieReserveRate {
		String name;
		float man, woman;
		float[] ages;

		public MovieReserveRate() {
			ages = new float[5];
		}

		public MovieReserveRate(String name, float man, float woman, float[] ages) {
			super();
			this.name = name;
			this.man = man;
			this.woman = woman;
			this.ages = ages;
		}

		public MovieReserveRate(String name, float man, float woman, float teen, float twenty, float thirty,
				float fourty, float fifty) {
			super();
			this.name = name;
			this.man = man;
			this.woman = woman;
			ages = new float[5];
			ages[0] = teen;
			ages[1] = twenty;
			ages[2] = thirty;
			ages[3] = fourty;
			ages[4] = fifty;
		}

		@Override
		public String toString() {
			return "[name=" + name + ", man=" + man + ", woman=" + woman + ", ages=" + Arrays.toString(ages) + "]";
		}

	}

	// 네이버 영화, cgv, 롯데시네마에서 긁어옴
	// 무비차트 목록에서 현재 상영작 영화의 코드 긁어옴
	// 긁어온 코드를 상세정보페이지에 넣어서 크롤링

	/*
	 * cgv http://www.cgv.co.kr/movies/?lt=1&ft=1
	 * http://www.cgv.co.kr/movies/detail-view/?midx=
	 */
	public static void main(String[] args) {

		List<MovieReserveRate> naver = new ArrayList<MovieReserveRate>();
		List<MovieReserveRate> lotte = new ArrayList<MovieReserveRate>();
		List<MovieReserveRate> cgv = new ArrayList<MovieReserveRate>();

//		crawlCgv(cgv);
		crawlNaver(naver);
//		crawlLotte(lotte);

	}

	private static void crawlCgv(List<MovieReserveRate> cgv) {
		try {
			// 영화 코드 목록을 담을 list
			List<String> codes = getCodes(".box-image", "a", "href", "midx");

			String detailUrl = "http://www.cgv.co.kr/movies/detail-view/?midx=";
			Document detailDoc = null;
			
			try {
				for (String code : codes) {
					System.out.println(detailUrl + code);
					detailDoc = Jsoup.connect(detailUrl + code).get();
					Elements graph1 = detailDoc.select("#jqplot_sex");
					Iterator<Element> spanTags = graph1.select("span").iterator();

					while (spanTags.hasNext()) {
						System.out.println(spanTags.next().text());
					}
				}
			} catch (Exception e) {
				// TODO: handle exception
			}

		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("error occured while crawling Cgv!");
		}
	}

	private static List<String> getCodes(String mvthumb, String tag, String tagUrl, String mvCode) throws IOException {
		String mainUrl = "https://www.cgv.co.kr/movies/?lt=1&ft=1";
		Document mainDoc = null;
		// document 가져온다
		mainDoc = Jsoup.connect(mainUrl).get();
		
		// 상영 중인 영화의 코드 목록을 가져온다
		Elements boxImage = mainDoc.select(mvthumb);
		Iterator<Element> tags = boxImage.select(tag).iterator();

		List<String> codes = new ArrayList<String>();
		
		// 영화 코드 담음
		while (tags.hasNext()) {
			String href = tags.next().absUrl(tagUrl);
			if (!href.contains(mvCode))
				continue;
			int split = href.indexOf("=") + 1;
			codes.add(href.substring(split));
		}
		return codes;
	}

	/*
	 * 네이버 영화 https://movie.naver.com/movie/running/current.naver
	 * https://movie.naver.com/movie/bi/mi/basic.naver?code=207922
	 */
	private static void crawlNaver(List<MovieReserveRate> naver) {
		String mainUrl = "https://movie.naver.com/movie/running/current.naver";
		Document mainDoc = null;

		try {
			// document 가져온다
			mainDoc = Jsoup.connect(mainUrl).get();

			// 상영 중인 영화의 코드 목록을 가져온다
			Elements boxImage = mainDoc.select(".thumb");
			Iterator<Element> aTags = boxImage.select("a").iterator();

			// 영화 코드 목록을 담을 list
			List<String> codes = new ArrayList<String>();

			// 영화 코드 담음
			while (aTags.hasNext()) {
				String href = aTags.next().absUrl("href");
				if (!href.contains("code"))
					continue;
				int split = href.indexOf("=") + 1;
				codes.add(href.substring(split));
			}

			String detailUrl = "https://movie.naver.com/movie/bi/mi/basic.naver?code=";
			Document detailDoc = null;
			try {
				for (String code : codes) {
					mainDoc = Jsoup.connect(detailUrl + code).get();
					System.out.println(mainDoc.toString());
//					Elements genderGraph = mainDoc.select("svg");
//					Iterator<Element> spanTags = genderGraph.select("tspan").iterator();
//					
//					while (spanTags.hasNext()) {
//						System.out.println(spanTags.next().text());
//					}
				}
			} catch (Exception e) {
				// TODO: handle exception
			}

			/*
			 * String url = "https://movie.naver.com/movie/bi/mi/basic.naver?code=81888";
			 * Document doc = null;
			 * 
			 * try { // document 가져온다 doc = Jsoup.connect(url).get();
			 * 
			 * // 목록을 가져온다 Elements elements1 = doc.select(".graph_box"); Iterator<Element>
			 * subject = elements1.select(".graph_box").iterator();
			 * 
			 * int i=0; for(Element el: elements1) { System.out.println((i+1)+": "+el); i++;
			 * }
			 * 
			 * } catch (Exception e){ System.out.println("error occured!"); }
			 */

		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("error occured while crawling Cgv!");
		}
	}

	/*
	 * 롯데시네마 https://www.lottecinema.co.kr/NLCHS/Movie/List?flag=1
	 * https://www.lottecinema.co.kr/NLCHS/Movie/MovieDetailView?movie=18651
	 */
	private static void crawlLotte(List<MovieReserveRate> naver) {
		String mainUrl = "www.cgv.co.kr/movies/?lt=1&ft=1";
		Document doc = null;

		try {
			// document 가져온다
			doc = Jsoup.connect(mainUrl).get();

			// 목록을 가져온다
			Elements elements1 = doc.select(".graph_percent");
			Iterator<Element> subject = elements1.select(".graph_box").iterator();

			int i = 0;
			for (Element el : elements1) {
				System.out.println((i + 1) + ": " + el);
				i++;
			}

		} catch (Exception e) {
			System.out.println("error occured while crawling Lotte!");
		}
	}

	public static String getCGVURL(int MovieCode) {
		String params = "code=" + MovieCode;

		return params;
	}

}
