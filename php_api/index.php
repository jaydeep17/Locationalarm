<?php
if(isset($_POST['tag']) && $_POST['tag'] != ' '){
    $tag = $_POST['tag'];
    //echo "inside main ";
    require_once __DIR__ . '/db_functions.php';
    require_once __DIR__ . '/db_config.php';
    
    $db = new DB_Functions();
    //echo "every thing declared ";
    $response = array("tag" => $tag, "success" => 0, "error" => 0);
    
    if($tag == DB_LOGIN){
	$email = $_POST['email'];
	$password = $_POST['password'];
	
	$user = $db->getUser($email,$password);
	if($user != false){
	    $response["success"] = 1;
	    $response["user"]["name"] = $user["name"];
            $response["user"]["email"] = $user["email"];
            $response["user"]["created_at"] = $user["created_at"];
            $response["user"]["updated_at"] = $user["updated_at"];
            echo json_encode($response);
	} else {
	    $response["error"] = 1;
	    $response["error_msg"] = "Incorrect email or password";
	    echo json_encode($response);
	}
    } elseif($tag == DB_REGISTER){
	//echo "inside register ";
	$name = $_POST['name'];
        $email = $_POST['email'];
        $password = $_POST['password'];
        
        if($db->isUserRegistered($email)){
	    $response["error"] = 2;
	    $response["error_msg"] = "User already existed";
            echo json_encode($response);
        } else {
	    $user = $db->storeUser($name, $email, $password);
	    //echo "I don't knw ";
	    $fixed_email = fixText($email);
	    $db->createUserTables($fixed_email);
            if ($user) {
		//echo " success ";
                $response["success"] = 1;
                $response["user"]["name"] = $user["name"];
                $response["user"]["email"] = $user["email"];
                $response["user"]["created_at"] = $user["created_at"];
                $response["user"]["updated_at"] = $user["updated_at"];
                $response["fixed_email"] = $fixed_email;
                echo json_encode($response);
                
            } else {
		//echo "stuck ";
                $response["error"] = 1;
                $response["error_msg"] = "Error occured in Registartion";
                echo json_encode($response);
            }
        }
    } elseif($tag == DB_SYNC){ 
	$result = $db->syncTable($_POST[DB_SYNC]);
	if($result){
	    $response["success"] = 1;
	} else {
	    $response["error"] = 1;
	}
	echo json_encode($response);
    } else {
	echo "INVALID REQUEST";
    }
} else {
    echo "Access Denied";
}


function fixText($name){
    /*if(!Normalizer::isNormalized($name)){
	$name = Normalizer::normalize($name);
    }*/
    $name = preg_replace("![^a-z0-9]+!i", "_", $name);
    //echo $name;
    return $name;
}
?>