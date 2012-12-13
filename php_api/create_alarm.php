<?php

$response = array();

if(isset($_POST['title']) && isset($_POST['description']) && isset($_POST['radius']) && isset($_POST['longitude']) && isset($_POST['latitude']) && isset($_POST['set_by'])){
    $title = $_POST['title'];
    $desc = $_POST['description'];
    $radius = $_POST['radius'];
    $longitude = $_POST['longitude'];
    $latitude = $_POST['latitude'];
    $set_by = $_POST['set_by'];
    
    require_once __DIR__ . '/db_connect.php';
    
    $db = new DB_CONNECT();
    
    $result = mysql_query("INSERT INTO alarms(title, description, radius, longitude, latitude, set_by) VALUES('$title', '$desc', $radius, $longitude, $latitude, '$set_by');");
    
    if($result){
	$response["success"] = 1;
	$response["message"] = "Alarm Created";
    } else {
	$response["success"] = 0;
	$response["message"] = "Failed to create an Alarm";
    }
    
} else {
    $response["success"] = 0;
    $response["message"] = "Required field(s) is missing";
}

echo json_encode($response);

?>