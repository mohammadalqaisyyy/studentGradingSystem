<%@ page import="java.util.List" %>
<!DOCTYPE html>
<html>
<head>
    <title>Student Grading System</title>
    <style>
        html, body {
            font-family: Arial, sans-serif;
            background-color: #405D9B;
            background-image: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' width='315' height='315' viewBox='0 0 800 800'%3E%3Cg fill='none' stroke='%23293B62' stroke-width='1'%3E%3Cpath d='M769 229L1037 260.9M927 880L731 737 520 660 309 538 40 599 295 764 126.5 879.5 40 599-197 493 102 382-31 229 126.5 79.5-69-63'/%3E%3Cpath d='M-31 229L237 261 390 382 603 493 308.5 537.5 101.5 381.5M370 905L295 764'/%3E%3Cpath d='M520 660L578 842 731 737 840 599 603 493 520 660 295 764 309 538 390 382 539 269 769 229 577.5 41.5 370 105 295 -36 126.5 79.5 237 261 102 382 40 599 -69 737 127 880'/%3E%3Cpath d='M520-140L578.5 42.5 731-63M603 493L539 269 237 261 370 105M902 382L539 269M390 382L102 382'/%3E%3Cpath d='M-222 42L126.5 79.5 370 105 539 269 577.5 41.5 927 80 769 229 902 382 603 493 731 737M295-36L577.5 41.5M578 842L295 764M40-201L127 80M102 382L-261 269'/%3E%3C/g%3E%3Cg fill='%235278C8'%3E%3Ccircle cx='769' cy='229' r='7'/%3E%3Ccircle cx='539' cy='269' r='7'/%3E%3Ccircle cx='603' cy='493' r='7'/%3E%3Ccircle cx='731' cy='737' r='7'/%3E%3Ccircle cx='520' cy='660' r='7'/%3E%3Ccircle cx='309' cy='538' r='7'/%3E%3Ccircle cx='295' cy='764' r='7'/%3E%3Ccircle cx='40' cy='599' r='7'/%3E%3Ccircle cx='102' cy='382' r='7'/%3E%3Ccircle cx='127' cy='80' r='7'/%3E%3Ccircle cx='370' cy='105' r='7'/%3E%3Ccircle cx='578' cy='42' r='7'/%3E%3Ccircle cx='237' cy='261' r='7'/%3E%3Ccircle cx='390' cy='382' r='7'/%3E%3C/g%3E%3C/svg%3E");
            margin: 0;
            padding: 0;
            display: flex;
            justify-content: center;
            align-items: center;
            height: 100vh;
        }

        body {
            flex-direction: column;
        }

        header {
            background-color: #405D9B;
            color: #fff;
            padding: 20px;
            text-align: center;
        }

        nav {
            background-color: #2A3F6D;
            color: #fff;
            display: flex;
            flex-direction: column; /* Display buttons vertically */
            justify-content: center;
            padding: 10px;
        }

        nav a {
            color: #fff;
            text-decoration: none;
            padding: 8px 16px;
            margin: 5px 0; /* Add vertical margin */
            border-radius: 5px;
            transition: background-color 0.3s;
        }

        nav a:hover {
            background-color: #3B548A;
        }

        .menu-content {
            padding: 20px;
            display: none; /* Initially hidden */
        }

        .menu-content.show {
            display: block;
        }

        form {
            max-width: 400px;
            margin: 0 auto;
            background-color: #fff;
            padding: 20px;
            border-radius: 10px;
            box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
        }

        label {
            display: block;
            margin-bottom: 5px;
            font-weight: bold;
        }

        input[type="text"],
        input[type="email"],
        textarea {
            width: 100%;
            padding: 10px;
            margin-bottom: 10px;
            border: 1px solid #ccc;
            border-radius: 5px;
            resize: vertical;
        }

        input[type="submit"] {
            padding: 10px 15px;
            background-color: #405D9B;
            color: #fff;
            border: none;
            border-radius: 5px;
            cursor: pointer;
            width: 100%;
        }

        input[type="submit"]:hover {
            background-color: #2A3F6D;
        }

        footer {
            background-color: #405D9B;
            color: #fff;
            padding: 10px;
            text-align: center;
        }
    </style>
</head>
<body>
<header>
    <h1>Welcome ${type} ${name}</h1>
</header>

<nav>
    <%
        int index = 1;
        List<String> menu = (List<String>) request.getAttribute("menu");
        for (String item : menu) {
            String contentId = "content" + index;
            index++;
    %>
    <a href="#" onclick="toggleContent('<%= contentId %>')"><%= item%></a>
    <%
        }
    %>
</nav>


<div class="menu-content" id=<%= "content1" %>>
    <%
        StringBuilder courses = (StringBuilder) request.getAttribute("courses");
        String coursesWithLineBreaks = courses.toString().replace("\n", ", ");
    %>
    <form>
        <h3><%= coursesWithLineBreaks %></h3>
    </form>
</div>

<div class="menu-content" id=<%= "content" + 2 %>>
    <form action="/login">
        <p>Are you sure?</p>
        <input type="submit" value="yes" />
    </form>
</div>

<footer>
    <p>&copy; Developed by Mohammad Al-Qaisy for Atypon Internship.</p>
</footer>

<script>
    function toggleContent(contentId) {
        var contentElement = document.getElementById(contentId);
        var allMenuContents = document.getElementsByClassName("menu-content");

        for (var i = 0; i < allMenuContents.length; i++) {
            if (allMenuContents[i].id === contentId) {
                allMenuContents[i].classList.toggle("show");
            } else {
                allMenuContents[i].classList.remove("show");
            }
        }
    }
</script>
</body>
</html>