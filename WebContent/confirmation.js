function displayMovie(sale_id, movie_id, title, quantity, price, totalAmount) {
    var movieHtml = `
        <tr>
            <td>${sale_id}</td>
            <td>${movie_id}</td>
            <td>${title}</td>
            <td>
                <span>${quantity}</span>
            </td>
            <td><a>${price}</a></td>
            <td><a>${price * quantity}</a></td>
        </tr>
    `;
    var $movieRow = $(movieHtml);
    $("#sale-list tbody").append($movieRow);

    // Update the total amount field
    $("#total-amount").text(totalAmount.toFixed(2));

}

function generateRandomSalesID() {
    const length = 8;
    const characters = 'ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789';
    let result = '';
    for (let i = 0; i < length; i++) {
        result += characters.charAt(Math.floor(Math.random() * characters.length));
    }
    return result;
}

$.ajax({
    type: "POST",
    url: "api/shoppingcart",
    data: {
        action: "confirmed_sale"
    },
    dataType: "json",
    success: function(movieCart) {
        var total_amount = 0;
        var price = 6.0;
        for (const movie_id in movieCart) {
            const movieItem = movieCart[movie_id];
            const title = movieItem.title;
            const quantity = movieItem.quantity;
            total_amount += price * quantity;
            const sale_id = generateRandomSalesID();
            displayMovie(sale_id, movie_id, title, quantity, price, total_amount);
        }
    },
    error: function(error) {
        console.error("Error:", error);
    }
});
