package chasqui.service.rest.response;

public class ImagenDePerfilResponse {

	private String avatar;

	public ImagenDePerfilResponse() {}
	public ImagenDePerfilResponse(String avatar) {
		this.setAvatar(avatar);
	}

	public String getAvatar() {
		return avatar;
	}

	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}
}
