<?php
$response = array();

require_once __DIR__ . '/db_connect.php';

$db = new DB_CONNECT();

$result = mysql_query("SELECT * FROM alarms");

if(mysql_num_rows($result) > 0){
    $response["alarms"] = array();
    
    while($row = mysql_fetch_array($result)){
	$alarm = array();
	$alarm["_id"] = $row["_id"];
	$alarm["title"] = $row["title"];
	$alarm["description"] = $row["description"];
	$alarm["radius"] = $row["radius"];
	$alarm["longitude"] = $row["longitude"];
	$alarm["latitude"] = $row["latitude"];
	$alarm["set_by"] = $row["set_by"];
	$alarm["created_at"] = $row["created_at"];
	$alarm["modified_at"] = $row["modified_at"];
	
	array_push($response["alarms"],$alarm);
    }
    
    $response["success"] = 1;
    echo json_encode($response);
} else {
    $response["success"] = 0;
    $response["message"] = "No Alarms found";
 
    echo json_encode($response);
}
?>