<%--
  Created by IntelliJ IDEA.
  User: haw
  Date: 17/06/15
  Time: 12:44 م
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
  <head>
    <title></title>

  <style>
      .thumb {
          height: 75px;
          border: 1px solid #000;
          margin: 10px 5px 0 0;
      }
  </style>

  </head>
  <body>
        Hello Lads
        <h1>Testing Websocket With JavaWebsocket</h1>
        <div id="drop_zone" style="background-color:greenyellow;height:125px;width:375px;" >Drop files here</div>
        <div id="output"></div>

        <input type="text" id="my_message"/>
        <input type="button" id="send_button"/>
        <input type="button" id="send_first_part_button" value="send part 1"/>
        <input type="file" id="files" name="files"/>
        <output id="list"></output>


        <script type="text/javascript" src="testclient.js"></script>

  </body>
</html>
