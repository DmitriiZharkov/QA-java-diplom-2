package user;

import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.apache.http.HttpStatus.SC_OK;
import static org.apache.http.HttpStatus.SC_UNAUTHORIZED;
import static org.hamcrest.CoreMatchers.is;

public class ChangeUserTest {
    private CreateUser createUser;
    private UserStep userStep;
    private String accessToken;

    @Before
    public void setUp() {
        userStep = new UserStep();
        createUser = UserGenerator.getUserRandom();
    }

    @Test
    @DisplayName("Изменение данных пользователя с авторизацией")
    @Description("Проверяем что пользователь сменил email")
    public void changeUserLoginAuthorised() {
        ValidatableResponse createResponse = userStep.registerUser(createUser);
        accessToken = createResponse.extract().path("accessToken");
        ValidatableResponse response = userStep.changeUserAuthorised(UserGenerator.getUserRandom(), accessToken);
        response
                .statusCode(SC_OK)
                .body("success", is(true));
    }

    @Test
    @DisplayName("Изменение данных пользователя без авторизации")
    @Description("Проверяем что пользователь сменил имя")
    public void changeUserEmailAuthorised() {
        userStep.registerUser(createUser);
        ValidatableResponse response2 = userStep.changeUserWithoutAuthorised(UserGenerator.getUserRandom());
        response2
                .assertThat()
                .statusCode(SC_UNAUTHORIZED)
                .body("success", is(false))
                .and()
                .body("message", is("You should be authorised"));
    }

    @After
    @DisplayName("Удаляем юзера")
    public void deleteUser() {
        if (accessToken != null) {
            UserStep.userDelete(accessToken);
        }
    }
}