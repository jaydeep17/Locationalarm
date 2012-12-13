<?php
class DB_Functions {
    private $db;
    private $con;

    function __construct(){
	require_once __DIR__ . '/db_connect.php';
	$this->db = new DB_CONNECT();
	$con = $this->db->connect();
    }
    
    public function storeUser($name, $email, $password){
	$result = mysql_query("INSERT INTO users(name, email, encrypted_password, salt, created_at, updated_at) VALUES('$name','$email','$password','salt', NOW(), NOW())");
	if($result){
	    $uid = mysql_insert_id();
	    $result = mysql_query("SELECT * FROM users WHERE uid = $uid");
	    return mysql_fetch_array($result);
	} else {
	    return false;
	}
    }
    
    
    public function createUserTables($name){
    
	$alarm_table = $name . "_alarms";
	$result1 = mysql_query("CREATE TABLE $alarm_table(_id int(11) primary key auto_increment, title varchar(100) not null, description text, radius varchar(4) not null, location varchar(50) not null, set_by varchar(30) not null, created_at timestamp default CURRENT_TIMESTAMP, updated_at timestamp);");
	
	$friend_table = $name . "_friends";
	$result2 = mysql_query("CREATE TABLE $friend_table(_id int(11) primary key auto_increment, name varchar(225) not null, email varchar(225) not null);");
    }
    
    public function isUserRegistered($email){
	$result = mysql_query("SELECT email from users WHERE email = $email");
	$no_of_rows = mysql_num_rows($result);
	if($no_of_rows > 0){
	    return true;
	} else {
	    return false;
	}
    }
    
    public function getUser($email, $password){
	$result = mysql_query("SELECT * FROM users WHERE email = $email") or die(mysql_error());
	$no_of_rows = mysql_num_rows($result);
	if($no_of_rows == 1){
	    $result = mysql_fetch_array($result);
	    return $result;
	} else {
	    return false;
	}
    }
    
    public function syncTable($qry){
	$result = mysql_query($qry);
	if($result == false){
	    return false;
	} else {
	    return true;
	}
    }
}
?>