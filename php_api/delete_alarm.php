<?php

$response = array();

if(isset($_POST['title'])){
    $title = $_POST['title'];
    
    require_once __DIR__ . '/db_connect.php';
    
    $db = new DB_CONNECT();
    
    $result = mysql_query("DELETE FROM alarms WHERE title = '$title'");
    
    if(mysql_affected_rows() > 0){
	$response["success"] = 1;
	$response["message"] = "Alarm deleted";
    } else {
	$response["success"] = 0;
	$response["message"] = "Alarm not found";
    }
    echo json_encode($response);
} else {
    $response["success"] = 0;
    $response["message"] = "Required field(s) is missing";
    echo json_encode($response);
}

?>