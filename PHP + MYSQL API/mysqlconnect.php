<?php
	$host = "localhost";   //Host
	$username = "id12195702_pumapp";    //User
	$password = "123456"; //Passsword
	$database = "id12195702_pumapp";     // Database Name
	 
	//creating a new connection object using mysqli 
	$conn = new mysqli($host, $username, $password, $database);
	 
	//if there is some error connecting to the database
	//with die we will stop the further execution by displaying a message causing the error.
	if ($conn->connect_error) {
		die("Database connection failed: " . $conn->connect_error);
	}
?>