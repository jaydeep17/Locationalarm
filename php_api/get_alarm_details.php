<?php
$response = array();

require_once __DIR__ . '/db_connect.php';

$db = new DB_CONNECT();

if(isset($_GET["title"])){
    $title = $_GET['title'];
    
    $result = mysql_query("SELECT * FROM alarms WHERE title = '$title'");
    
    if(!empty($result)){
	if(mysql_num_rows($result) > 0 ){
	    $result = mysql_fetch_array($result);
	    $alarm = array();
	    $alarm["_id"] = $result["_id"];
	    $alarm["title"] = $result["title"];
	    $alarm["description"] = $result["description"];
	    $alarm["radius"] = $result["radius"];
	    $alarm["longitude"] = $result["longitude"];
	    $alarm["latitude"] = $result["latitude"];
	    $alarm["set_by"] = $result["set_by"];
	    $alarm["created_at"] = $result["created_at"];
	    $alarm["modified_at"] = $result["modified_at"];
	    
	    $response["success"] = 1;
	    $response["alarm"] = array();
	    array_push($response["alarm"],$alarm);
	    echo json_encode($response);
	} else {
	    $response["success"] = 0;
	    $response["message"] = "No alarm found";
	    echo json_encode($response);
	}
    } else {
	    $response["success"] = 0;
	    $response["message"] = "No alarm found";
	    echo json_encode($response);
    }
} else {
    $response["success"] = 0;
    $response["message"] = "Required field(s) is missing";
    echo json_encode($response);
}
?>