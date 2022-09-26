import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.specification.RequestSpecification;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;

public class LoginToFakeStore {

    RequestSpecBuilder specBuilder = new RequestSpecBuilder();
    RequestSpecification reqSpec;
    public String accessToken = "";//= "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyRW1haWwiOiJkdW1teUBlbWFpbC5jb20iLCJpYXQiOjE2NjQyMTAyMzUsImV4cCI6MTY2NDM4MzAzNX0.dwn1j6pKrxq9RYPMVSo-GVuEZW9NhuhKKAsK5jIOugk";


    @BeforeClass
    public void setRequestSpecification() {
        specBuilder.setBaseUri("http://api.fakeshop-api.com/")
                .setContentType(ContentType.JSON);
        // .addHeader("Authorization", "Bearer " + accessToken);
        reqSpec = specBuilder.build();
    }

    //Sign Up to Store
    @Test(priority = 0)
    public void signUpForStore() {
        String response = given()
                .spec(reqSpec)
                .body(LoginPayload.loginCredPayload()) //or we can use .body(Payload.loginCredPayload())
                .when().post("users/signup")
                .then().log().all().extract().response().asString();
        JsonPath jPath = new JsonPath(response);
        accessToken = jPath.getString("token");
        System.out.println(accessToken);
    }


    //Login to Store
    @Test(priority = 1)
    public void loginToStore() {
        LoginPojo loginPojo = new LoginPojo();
        loginPojo.setEmail("dummy@email.com");
        loginPojo.setPassword("123456");

        String response = given()
                .spec(reqSpec)
                .body(LoginPayload.loginCredPayload()) //or we can use .body(Payload.loginCredPayload())
                .when().post("users/signin")
                .then().log().all().extract().response().asString();

        System.out.println(response);
        JsonPath jsonPath = new JsonPath(response);
        String tokenFromResponse = jsonPath.getString("token");
        System.out.println("This is from response  " + tokenFromResponse);

    }


    //Add a product in the cart.
    @Test(priority = 2)
    public void addProductToCart() {

        String addProductResponse = given()
                .spec(reqSpec).header("Authorization", "Bearer " + accessToken)
                .body(LoginPayload.productPayload())
                .when().post("carts/addToCart")
                .then().log().all().extract().response().asString();

        JsonPath jsPath = new JsonPath(addProductResponse);
        String productMessage = jsPath.getString("message");
        System.out.println("Product message " + productMessage);

    }

    //Add a product in the cart.
    @Test(priority = 3)
    public void removeProductFromTheCart() {

        String removedProductMessage = given()
                .spec(reqSpec).header("Authorization", "Bearer " + accessToken)
                .body(LoginPayload.productPayload())
                .when().post("carts/removeFromCart")
                .then().log().all().extract().response().asString();

        JsonPath jsPath = new JsonPath(removedProductMessage);
        String productMessage = jsPath.getString("message");
        System.out.println("Product removed " + productMessage);

    }

}
