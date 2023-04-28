function displayMovie(movie_id, title, quantity, price, totalAmount) {
    var movieHtml = `
        <tr>
            <td>${movie_id}</td>
            <td>${title}</td>
            <td>
                <button class="decrease-quantity">-</button>
                <span>${quantity}</span>
                <button class="increase-quantity">+</button>
            </td>
            <td><a>${price}</a></td>
            <td><a>${price * quantity}</a></td>
            <td>
                <button class="delete">Delete</button>
            </td>
        </tr>
    `;
    var $movieRow = $(movieHtml);
    $("#shopping-list tbody").append($movieRow);

    $movieRow.find(".decrease-quantity").on("click", function() {
        updateQuantity(movie_id, -1);
    });

    $movieRow.find(".increase-quantity").on("click", function() {
        updateQuantity(movie_id, 1);
    });

    $movieRow.find(".delete").on("click", function() {
        deleteItem(movie_id);
    });

    // Update the total amount field
    $("#total-amount").text(totalAmount.toFixed(2));

    $("#checkout-button").on("click", function() {
        const totalAmount = $("#total-amount").text();
        window.location.href = "checkout.html?total_amount=" + totalAmount;
    });
}

function updateQuantity(movie_id, change) {
    $.ajax({
        type: "POST",
        url: "api/shoppingcart",
        data: {
            action: "update_quantity",
            id: movie_id,
            change: change
        },
        success: function() {
            location.reload(); // Reload the page to refresh the cart display
        },
        error: function(error) {
            console.error("Error:", error);
        }
    });
}

function deleteItem(movie_id) {
    $.ajax({
        type: "POST",
        url: "api/shoppingcart",
        data: {
            action: "delete",
            id: movie_id
        },
        success: function() {
            location.reload(); // Reload the page to refresh the cart display
        },
        error: function(error) {
            console.error("Error:", error);
        }
    });
}


$.ajax({
    type: "POST",
    url: "api/shoppingcart",
    data: {
        action: "get_movie_cart"
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
            displayMovie(movie_id, title, quantity, price, total_amount);
        }
    },
    error: function(error) {
        console.error("Error:", error);
    }
});
