package order;

import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.junit.Before;
import org.junit.Test;
import user.CreateUser;
import user.LoginUser;
import user.UserGenerator;
import user.UserStep;

import static order.Ingredients.ingredients;
import static org.apache.http.HttpStatus.SC_UNAUTHORIZED;
import static org.hamcrest.CoreMatchers.is;
import static org.apache.http.HttpStatus.SC_OK;

public class OrderUserTest {
    private Order order;
    private OrderStep orderStep;
    private CreateUser createUser;
    private UserStep userStep;
    private LoginUser loginUser;
    private String accessToken;

    @Before
    public void setUp() {
        userStep = new UserStep();
        createUser = new UserGenerator().getUserRandom();
        orderStep = new OrderStep();
        order = new Order(ingredients);
        ValidatableResponse createResponse = userStep.registerUser(createUser);
        accessToken = createResponse.extract().path("accessToken");
    }

    @Test
    @DisplayName("Получение заказов авторизованного пользователя")
    public void receivingOrdersAuthorisedUser() {
        loginUser = new LoginUser(createUser.getEmail(), createUser.getPassword());
        OrderStep.createOrderAuthorised(order, "accessToken");
        ValidatableResponse responseOrder = orderStep.getOrderWithAuth(accessToken);
        responseOrder
                .assertThat()
                .statusCode(SC_OK)
                .body("success", is(true));
    }

    @Test
    @DisplayName("Получение заказов неавторизованного пользователя")
    public void receivingOrderUnauthorisedUser() {
        ValidatableResponse responseOrder = orderStep.createOrderWithoutAuth();
        responseOrder
                .assertThat()
                .statusCode(SC_UNAUTHORIZED)
                .body("success", is(false))
                .and()
                .body("message", is("You should be authorised"));
    }
}