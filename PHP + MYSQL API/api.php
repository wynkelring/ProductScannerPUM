<?php
	require_once 'mysqlconnect.php';
	$response = array();
	if(isset($_GET['products'])) {
		switch($_GET['products']){
			case 'all':
				$sql = "SELECT * FROM product";
				if ($result = mysqli_query($conn, $sql)){
					$tempArray = array();
					while($row = $result->fetch_object()){
						$tempArray = $row;
						array_push($response, $tempArray);
					}
				}
				$response = array('products'=>$response);
			break; 
			case 'subscription':
				if(isset($_GET['userKey'])) {
					$sql = 'SELECT p.name, p.ean, p.quantity FROM product p JOIN subscriptions s ON p.ean = s.ean WHERE userKey = "'.$_GET['userKey'].'"';
					if ($result = mysqli_query($conn, $sql)){
					    $tempArray = array();
					    while($row = $result->fetch_object()){
					    	$tempArray = $row;
					    	array_push($response, $tempArray);
					    }
				    }
				    $response = array('subscribe'=>$response);
				}
			break; 
		}
	}
	if(isset($_GET['product'])) {
				$sql = "SELECT * FROM product WHERE ean = " . $_GET['product'];
				if ($result = mysqli_query($conn, $sql)){
					$tempArray = array();
					while($row = $result->fetch_object()){
						$tempArray = $row;
						array_push($response, $tempArray);
					}
				}
				$response = array('product'=>$response);
	}
	if(isset($_GET['action']) && $_GET['action'] == 'add') {
	    $content = trim(file_get_contents("php://input"));
	    $decoded = json_decode($content, true);
	    if(isset($decoded['ean']) && isset($decoded['name']) && isset($decoded['quantity'])){
            $ean = $decoded['ean'];
            $name = $decoded['name'];
            $quantity = $decoded['quantity'];
            
            $stmt = $conn->prepare("SELECT name FROM product WHERE ean = ?");
            $stmt->bind_param("i", $ean);
            $stmt->execute();
            $stmt->store_result();
            if($stmt->num_rows == 0) {
                $stmt = $conn->prepare("INSERT INTO product (ean, name, quantity) VALUES (?, ?, ?)");
                $stmt->bind_param("isi", $ean, $name, $quantity);
                if($stmt->execute()){
                    $stmt = $conn->prepare("SELECT * FROM product WHERE ean = ?"); 
                    $stmt->bind_param("i",$ean);
                    $stmt->execute();
                    $stmt->bind_result($ean, $name, $quantity);
                    $stmt->fetch();
    
                    $user = array(
                        'ean'=>$ean, 
                        'name'=>$name, 
                        'quantity'=>$quantity
                    );
                    $response['error'] = false; 
                    $response['message'] = 'Pomyślnie dodano produkt.'; 
                    $response['user'] = $user; 
                } else {
                    $response['error'] = true; 
                    $response['message'] = 'Wystąpił nieoczekiwany błąd.'; 
                }
            } else {
                $response['error'] = true;
                $response['message'] = 'Produkt o podanym EAN już istnieje.';
            }
            $stmt->close();
        } else {
            $response['error'] = true; 
            $response['message'] = 'Brakuje niektórych danych.';
        }
	}
	if(isset($_GET['action']) && $_GET['action'] == 'subscribe') {
	    $content = trim(file_get_contents("php://input"));
	    $decoded = json_decode($content, true);
	    if(isset($decoded['ean']) && isset($decoded['subKey'])){
            $ean = $decoded['ean'];
            $subKey = $decoded['subKey'];
            
            $stmt = $conn->prepare("SELECT * FROM subscriptions WHERE ean = ? AND userKey = ?");
            $stmt->bind_param("is", $ean, $subKey);
            $stmt->execute();
            $stmt->store_result();
            if($stmt->num_rows == 0) {
                $stmt = $conn->prepare("INSERT INTO subscriptions (ean, userKey) VALUES (?, ?)");
                $stmt->bind_param("is", $ean, $subKey);
                if($stmt->execute()){
                    $response['error'] = false; 
                    $response['subscribedBefore'] = false; 
                } else {
                    $response['error'] = true; 
                    $response['message'] = 'Wystąpił nieoczekiwany błąd.'; 
                }
            } else {
                $response['error'] = false;
                $response['subscribedBefore'] = true; 
            }
            $stmt->close();
        } else {
            $response['error'] = true; 
            $response['message'] = 'Wysłano niepełne zapytanie';
        }
	}
	if(isset($_GET['action']) && $_GET['action'] == 'unsubscribe') {
	    $content = trim(file_get_contents("php://input"));
	    $decoded = json_decode($content, true);
	    if(isset($decoded['ean']) && isset($decoded['subKey'])){
            $ean = $decoded['ean'];
            $subKey = $decoded['subKey'];
            
            $stmt = $conn->prepare("SELECT * FROM subscriptions WHERE ean = ? AND userKey = ?");
            $stmt->bind_param("is", $ean, $subKey);
            $stmt->execute();
            $stmt->store_result();
            if($stmt->num_rows == 0) {
                $response['error'] = false;
                $response['unsubscribed'] = false; 
            } else {
                $stmt = $conn->prepare("DELETE FROM subscriptions WHERE ean = ? AND userKey = ?");
                $stmt->bind_param("is", $ean, $subKey);
                if($stmt->execute()){
                    $response['error'] = false; 
                    $response['unsubscribed'] = true; 
                } else {
                    $response['error'] = true; 
                    $response['message'] = 'Wystąpił nieoczekiwany błąd.'; 
                }
            }
            $stmt->close();
        } else {
            $response['error'] = true; 
            $response['message'] = 'Wysłano niepełne zapytanie';
        }
	}
echo json_encode($response);
?>