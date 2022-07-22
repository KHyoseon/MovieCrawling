import java.util.Arrays;

public class MovieInfoDto {
	String code, title, ranking;
	String man, woman;
	String[] ages;
	Detail detail;

	public MovieInfoDto() {
		ages = new String[5];
		detail = new Detail();
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
	
	public String getRanking() {
		return ranking;
	}

	public void setRanking(String ranking) {
		this.ranking = ranking;
	}

	public String getMan() {
		return man;
	}

	public void setMan(String man) {
		this.man = man;
	}

	public String getWoman() {
		return woman;
	}

	public void setWoman(String woman) {
		this.woman = woman;
	}

	public String[] getAges() {
		return ages;
	}

	public void setAges(String[] ages) {
		this.ages = ages;
	}
	
	public Detail getDetail() {
		return detail;
	}

	public void setDetail(Detail detail) {
		this.detail = detail;
	}

	@Override
	public String toString() {
		return "MovieInfoDto [code=" + code + ", title=" + title + ", ranking=" + ranking + ", man=" + man + ", woman="
				+ woman + ", ages=" + Arrays.toString(ages) + "]";
	}

}
