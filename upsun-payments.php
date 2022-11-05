<?php

	$data = new StdClass();

	$amount = (int)$_POST['amount'];
    $apiKey = "rzp_test_Jgq3YJujYra6Lp";  
    //live      rzp_live_MK7VK9E_5wa1ckl
    //test      rzp_test_Jgq3YJujYra6Lp
    $apiKeyEnc = "cnpwX3Rlc3RfSmdxM1lKdWpZcmE2THA6OFl1ZjQwRlc0UjA3TnFlbE1tSnZvbVR6";
     //live     cnpwX2xpdmVfTUs3Vks5RV81d2ExY2tsOjgxSzZ1QTg4QWxrcnBzcmR6UlpWVVNB
     //test     cnpwX3Rlc3RfSmdxM1lKdWpZcmE2THA6OFl1ZjQwRlc0UjA3TnFlbE1tSnZvbVR6
    
    $curl = curl_init();

    curl_setopt_array($curl, array(
        CURLOPT_URL => 'https://api.razorpay.com/v1/orders',
        CURLOPT_RETURNTRANSFER => true,
        CURLOPT_ENCODING => '',
        CURLOPT_MAXREDIRS => 10,
        CURLOPT_TIMEOUT => 0,
        CURLOPT_FOLLOWLOCATION => true,
        CURLOPT_HTTP_VERSION => CURL_HTTP_VERSION_1_1,
        CURLOPT_CUSTOMREQUEST => 'POST',
        CURLOPT_POSTFIELDS =>'{
        "amount": '.$amount.',
        "currency": "INR",
        "receipt": "UPSUN"
        }',
        CURLOPT_HTTPHEADER => array(
            'content-type:  application/json',
            'Authorization: Basic '.$apiKeyEnc // keys
        ),
    ));
            
    $responseStr = curl_exec($curl);
            
    curl_close($curl);
            
    $response = json_decode($responseStr, true);
            
    $paymentId = $response['id'];
    
    $data->api_key = $apiKey;
    $data->paymentId = $paymentId;
    $data->paymentDetails = $response; 
    $data->message = "Paynebt details.";
    $data->status = true;
	if (!$paymentId){
     	$data->message = $response['error']['description'];
    	$data->status = false;
    }
    
    
	echo json_encode($data);
    
?>