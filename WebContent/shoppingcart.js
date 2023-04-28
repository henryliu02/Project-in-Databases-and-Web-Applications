function displayMovie(movie_id, title, quantity) {
    var movieHtml = `
        <tr>
            <td>${movie_id}</td>
            <td>${title}</td>
            <td>
                <button class="decrease-quantity">-</button>
                <span>${quantity}</span>
                <button class="increase-quantity">+</button>
            </td>
            <td><a>$6.0</a></td>
            <td><a>$6.0</a></td>
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
        for (const movie_id in movieCart) {
            const movieItem = movieCart[movie_id];
            const title = movieItem.title;
            const quantity = movieItem.quantity;
            displayMovie(movie_id, title, quantity);
        }
    },
    error: function(error) {
        console.error("Error:", error);
    }
});
