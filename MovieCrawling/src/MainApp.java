import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import com.codoid.products.exception.FilloException;
import com.codoid.products.fillo.Connection;
import com.codoid.products.fillo.Fillo;

public class MainApp {
	public static final String WEB_DRIVER_ID = "webdriver.chrome.driver"; // �뱶�씪�씠踰� ID
	public static final String WEB_DRIVER_PATH = "C:\\drivers\\chromedriver.exe"; // �뱶�씪�씠踰� 寃쎈줈

	/*
	 * cgv http://www.cgv.co.kr/movies/?lt=1&ft=1
	 * http://www.cgv.co.kr/movies/detail-view/?midx=
	 */
	public static void main(String[] args) throws FilloException {

		List<MovieInfoDto> naver = new ArrayList<MovieInfoDto>();

		try {
			System.setProperty(WEB_DRIVER_ID, WEB_DRIVER_PATH);
		} catch (Exception e) {
			e.printStackTrace();
		}

		ChromeOptions options = new ChromeOptions();
//		options.addArguments("headless");

		WebDriver driver = new ChromeDriver(options);

		String url = "https://movie.naver.com/movie/sdb/rank/rmovie.naver";
		driver.get(url);

		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
		}


		for (int i = 0; i < 10; i++) {
			MovieInfoDto curMovie = new MovieInfoDto();
			
			List<WebElement> movies = driver.findElements(By.className("tit3"));
			WebElement anchor = movies.get(i).findElements(By.tagName("a")).get(0);

			// 영화명
			curMovie.setTitle(anchor.getAttribute("title"));

			// 영화 코드
			String link = anchor.getAttribute("href");
			int split = link.indexOf("=") + 1;
			curMovie.setCode(link.substring(split));
			
			anchor.click();
			
			// 영화 정보
			WebElement mvInfo = driver.findElements(By.className("mv_info")).get(1);

			// 영어 제목
			curMovie.detail.setEngTitle(mvInfo.findElement(By.className("h_movie2")).getText());

			// 포스터
			WebElement poster = driver.findElements(By.className("poster")).get(1);
			WebElement src = poster.findElement(By.tagName("img"));
			curMovie.detail.setImg(src.getAttribute("src"));
			
			// 관람객 평점
			
			// .dd span a
			List<WebElement> infoSpec = mvInfo.findElements(By.tagName("dd"));
			List<WebElement> spanA = null;
			int j=0;
			spanA = mvInfo.findElements(By.tagName("span"));
			try {
				spanA = mvInfo.findElements(By.tagName("a"));
			}catch (Exception e) {
			}finally {
				if(spanA == null) {
					curMovie.detail.setRunningTime(spanA.get(j).getText());
				}
			}
			
			// 장르
			curMovie.detail.setGenere(infoSpec.);
			
			// 국가
			curMovie.detail.setCountry(mvInfo.fin);
			
			// 러닝타임
			
			// 개봉일
			
			// 감독
			
			// 출연
			
			// 등급
			
			// 누적관객


			// 줄거리
			
			// 페이지 진입 대기
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
			}

			WebElement genderGraph = null;
			try {
				// #actualGenderGraph tspan 성별 예매율
				genderGraph = driver.findElement(By.id("actualGenderGraph"));
			} catch(Exception e) {
			} finally {
				if(genderGraph == null) {
					driver.navigate().back();
					continue;
				}
			}
			
			List<WebElement> genderPercent = genderGraph.findElements(By.tagName("tspan"));
			curMovie.setMan(genderPercent.get(0).getText());
			curMovie.setWoman(genderPercent.get(1).getText());
			
			List<WebElement> ageGraph = driver.findElements(By.className("bar_graph"));
			List<WebElement> agePercent = ageGraph.get(1).findElements(By.className("graph_percent"));

			String[] age = new String[5];
			for (int j = 0; j< agePercent.size(); j++) {
				age[j] = agePercent.get(j).getText();
			}
			curMovie.setAges(age);
			naver.add(curMovie);
			
			driver.navigate().back();
		}

		try {
			if (driver != null) {
				driver.close();

				driver.quit();
			}
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage());
		}
		
		Fillo fillo = new Fillo();
		Connection connection = fillo.getConnection("C:\\Users\\multicampus\\crawling\\test.xlsx");
		
//		String strInsertQuerry = String.format(
//		"INSERT INTO Sheet1 (movieCode	title, male10, male20, male30, male40, male50, female10, female20, female30, female40, female50, rating) "
//				+ "Values('%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s')", );
//		connection.executeUpdate(strInsertQuerry);
//		connection.close();
		
	}

	private static void crawlNaver(List<MovieInfoDto> naver) {
		String mainUrl = "https://movie.naver.com/movie/running/current.naver";
		Document mainDoc = null;

		try {
			mainDoc = Jsoup.connect(mainUrl).get();

			Elements boxImage = mainDoc.select(".thumb");
			Iterator<Element> aTags = boxImage.select("a").iterator();

			List<String> codes = new ArrayList<String>();

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
					Elements genderGraph = mainDoc.select("svg");
					Iterator<Element> spanTags = genderGraph.select("tspan").iterator();

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

	/*
	 * String url = "https://movie.naver.com/movie/bi/mi/basic.naver?code=81888";
	 * Document doc = null;
	 * 
	 * try { // document 媛��졇�삩�떎 doc = Jsoup.connect(url).get();
	 * 
	 * // 紐⑸줉�쓣 媛��졇�삩�떎 Elements elements1 = doc.select(".graph_box");
	 * Iterator<Element> subject = elements1.select(".graph_box").iterator();
	 * 
	 * int i=0; for(Element el: elements1) { System.out.println((i+1)+": "+el); i++;
	 * }
	 * 
	 * } catch (Exception e){ System.out.println("error occured!"); }
	 */

}