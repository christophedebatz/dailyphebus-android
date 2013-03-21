<?php

class Phebus {
	
	const host = 'www.phebus.tm.fr';
	const timesURL = '/sites/all/themes/mine/phebus/itineraire/IframeRendu.php';
	const stopsURL = '/sites/all/themes/mine/phebus/itineraire/AppelArret.php';
	
	const normalDirection = 1;
	const opposedDirection = 2;
	const showAll = -1;
	
	private $listBusLines = array (
			'A' => '0000A/A/3/Versailles Satory - Le Chesnay Hôpital A/0/',
			'B' => '0000B/B/3/Versailles Porchefontaine - Rocquencourt/0/',
			'C' => '0000C/C/3/Europe - Petits Bois/0/',
			'D' => '0000D/D/3/Versailles rive gauche - Viroflay rive g/0/',
			'E' => '0000E/E/3/Lycée Jules Ferry - Picardie/0/',
			'F' => '0000F/F/3/Versailles rive gauche - Vaucresson gare/0/',
			'G' => '0000G/G/3/Gare des Chantiers - Pershing/0/',
			'H' => '0000H/H/3/Versailles rive gauche - La Celle-Saint-/0/',
			'I' => '0000I/I/3/Vaucresson Gare SNCF - Garches Gare SNCF/0/',
			'J' => '0000J/J/3/Vélizy Maryse Bastié - Saclay HEC/0/',
			'K' => '0000K/K/3/Gare des Chantiers - Satory Parc d Activ/0/',
			'L' => '0000L/L/3/Versailles rive gauche - Saclay Val d Al/0/',
			'N' => '0000M/M/3/Le Chesnay Passy - Vaucresson Gare SNCF/0/',
			'O' => '0000N/N/3/Jouy Gare - Inra - Jouy Campus HEC/0/',
			'P' => '0000O/O/3/Stade de Porchefontaine - Place Laboulay/0/',
			'Q' => '0000P/P/3/Gare de Saint-Cyr l école - Inra - Gare/0/',
			'R' => '0000R/R/3/Gare des Chantiers - Université/0/',
			'S' => '0000S/S/3/Versailles rive droite - Vaucresson Gare/0/',
			'U' => '0000U/U/3/Viroflay rive gauche - Le Chesnay Parly/0/',
			'V' => '0000V/V/3/Le Chesnay Parly 2 - Ville d Avray Eglis/0/',
			'W' => '0000W/W/3/Gare des Chantiers - Satory Office Parc/0/',
			'Z' => '0000Z/Z/3/Versailles rive gauche - Saclay Le Chris/0/',
			'00N1' => '000N1/00N1/3/Gare la Celle - Gare des Chantiers/0/',
			'ARC' => '00ARC/ARC/3/Satory Zone Technique - Collège Rameau/0/',
			'BAK' => '00BAK/BAK/3/Stade de Porchefontaine - Lycée Jules Fe/0/',
			'HEx' => '00HEX/HEx/3/Versailles Chantiers - Gare La Celle-Sai/0/',
			'JLB' => '00JLB/JLB/3/Buc Ces ML King - Jouy Metz la Mare/0/',
			'LAB' => '00LAB/LAB/3/Garches Mairie - Versailles Pl Laboulay/0/',
			'LFA' => '00LFA/LFA/3/Garches Mairie - Buc Franco Allemand/0/',
			'TEx' => '00TEX/TEx/3/Versailles Chantiers - Louveciennes Vill/0/',
			'TRI' => '00TRI/TRI/3/Chantiers - rive gauche - rive droite/0/',
			'YEx' => '00YEX/YEx/3/Versailles Université - Saint Quentin en Yve/0/'
		);
		
	
	private $direction; // 1 = ordre 2 = desordre
	private $busLine;
	private $busStop = 0;
	private $date;
	
	
	
	public function __construct ($direction = Phebus::normalDirection, $date = 0, $busLine = 0) {
		if ($busLine)
			$this->setBusLine($busLine);
		
		$this->date = new Date($date);		
		$this->setDirection($direction);
	}
	
	
	
	
	public function setDirection ($direction = Phebus::normalDirection) {
		$this->direction = (int) $direction;
	}
	
	public function getDirection () {
		return $this->direction;
	}
	
	
	public function showDirection () {
		return ($this->direction == Phebus::normalDirection) ? 'direction normale' : 'direction opposee';
	}
	
	public function setBusLine ($busLine) {
		$this->busLine = strval($busLine);
	}
	
	public function getBusLine () {
		return $this->busLine;
	}
	
	public function showBusLine () {
		$arrayDir1 = explode("/", $this->busLine);
		$arrayDir2 = explode("-", $arrayDir1[3]);
		array_map('trim', $arrayDir2);
		
		$result = 'ligne ' . $arrayDir1[1] . ', ';
		if ($this->direction == Phebus::normalDirection)
			$result .= $arrayDir2[0] . ' vers ' . $arrayDir2[1];
		else
			$result .= $arrayDir2[1] . ' vers ' . $arrayDir2[0];
			
		return $result;
	}

	
	public function setBusStop ($busStop) {
		$this->busStop = strval($busStop);
	}
	
	public function getBusStop () {
		return $this->busStop;
	}
	
	public function setDate ($date) {
		$this->date = $date;
	}
	
	public function getDate () {
		return $this->date;
	}
	

	
	
	public function setBusLineByLetter ($letter) {
		if (array_key_exists($letter, $this->listBusLines)) {
			$this->setBusLine($this->listBusLines[$letter]);
			return true;
		}
		else
			return false;
	}
	
	
	public function sendBusLines ($display = true) {
		$busLinesArray = array();
		$currentBusLine = '';
		foreach ($this->listBusLines as $letter => $value) {
			$split1 = explode("/", $value);
			$split2 = explode("-", $split1[3]);

			$currentInfoBusLine = array(
								'letter' => $split1[1],
								'firstExtremity' => trim($split2[0]),
								'secondExtremity' => trim($split2[1])
							);
			$busLinesArray[] = $currentInfoBusLine;
		}
		if ($display)
			echo json_encode(array('busLineList' => $busLinesArray));
		
		return $busLinesArray;
	}
	
	
	public function sendBusStops ($display = true) {
		$busStopsArray = $this->getListOfBusStops();
		if ($display)
			echo json_encode(array('busStopList' => $busStopsArray));
	}
	
	
	
	public function getListOfBusStops () {
		if (!$this->busLine)
			return 'no busline';
		
		$data = 'ligne=' . $this->busLine;
		$query = $this->makeHTTPQuery(Phebus::host, Phebus::stopsURL, $data);
		
		try {
			$socket = socket_create (AF_INET, SOCK_STREAM, SOL_TCP);
			
			if ($socket < 0)
       			throw new Exception (socket_strerror($socket));
				
			if (socket_connect ($socket, gethostbyname(Phebus::host), 80) < 0)
       			throw new Exception ('failed to connect to ' . Phebus::host);
			
			if (($int = socket_write ($socket, $query, strlen($query))) === false)
        		throw new Exception ('failed to write socket (' . $int . ' car written)');
			
			$result = '';
			while($buffer = socket_read ($socket, 2000))
				$result .= $buffer;
			
			socket_close($socket);

			$result = explode("text/html", $result);
			$result = json_decode($result[1], true);
			unset($result['text'], $result['value'][count($result['value']) - 1]);

			$new_result = array();
			for ($k = 0; $k < count($result['value']); $k++) {
				$temp = explode("/", $result['value'][$k]);
				$new_result[$temp[0]] = $this->convertAccents($temp[1]);
			}
			
			$final_array = array();
			foreach ($new_result as $code => $name) {
				$currentStop = array(
						'name' => $name,
						'code' => $code
					);
				$final_array[] = $currentStop;
			}
			
			return $final_array;

		} catch (Exception $e) {
			exit('getListOgBusLinesError: ' . $e->getMessage());
		}
	}
	
	
	
	
	
	public function getArrayOfTimes ($fromHour = -1) {
		if (!$this->busLine || !$this->busStop)
			return 'no bus line or bus stop';
		
		if ($this->direction == Phebus::opposedDirection) {
			$temp = explode("/", $this->busLine);
			$addingCode = '&strArg=' . $this->busStop . '#' . 
									$temp[0] . '#' . 
									strval($this->date->getYear() . $this->date->getMonth() . $this->date->getDay());
		} else
			$addingCode = '';
			
		$data = 'ligneDepart=' . $this->busLine . '&arretDepart=' . $this->busStop . '&ligneArrivee=&arretArrivee=&argSens=' . $this->direction . '&selectionDate=' . $this->date->getDate() . '&ligneArrivee=&arretArrivee=&horaire1=Horaire' . $addingCode;
		

		$query = $this->makeHTTPQuery(Phebus::host, Phebus::timesURL, $data);
		
		try {
			$socket = socket_create (AF_INET, SOCK_STREAM, SOL_TCP);
			
			if ($socket < 0)
       			throw new Exception (socket_strerror($socket));
				
			if (socket_connect ($socket, gethostbyname(Phebus::host), 80) < 0)
       			throw new Exception ('failed to connect to ' . Phebus::host);
			
			if (($int = socket_write ($socket, $query, strlen($query))) === false)
        		throw new Exception ('failed to write socket (' . $int . ' car written)');
			
			$result = '';
			while($buffer = socket_read ($socket, 2000))
				$result .= $buffer;

			socket_close($socket);
			
			$result = explode("<!-------------- PHP ---------------->", $result);
			if (trim($result[1]) == "Pas d'horaire<br/></div>")
				exit;
				
			$div_array = array();
			$div_array = explode("<font size=2><b>24</b></td>	</tr>	", $result[1]);
			$div_array =  explode("</td>	</tr></table></table>", $div_array[1]);

			$result =  preg_replace("#align='?CENTER'?#", '', $div_array[0]);
			$result = str_replace("style='background-color:#fcae06;' ", '', $result);
			$result = preg_replace("#(&nbsp;|\s)*#", '', $result);
			$result = preg_replace("#\s*[0-9a-z]{3,}\s*#", "", $result);
			$result = '<table>' .str_replace("\t", "", $result) . '</table>';

			$result = $this->parseHtmlTableToPhpArray ($result);
			
			$size = count($result);
			
			
			if ($fromHour > 0 && $fromHour <= 24)
			{
				$fromHour--;
				
				for ($i = 0; $i < $size; $i++) {
					if (!empty($result[$i][$fromHour]))
						echo $result[$i][$fromHour] . '|';
				}
			}
			else if ($fromHour == Phebus::showAll) {
				
				for ($h = 0; $h < 24; $h++) {
					if ($h > 0) echo ';';
					echo $h + 1;
					for ($i = 0; $i < $size	; $i++) {
						if (!empty($result[$i][$h])) {
							echo '|' . $result[$i][$h];
						} else {
							break;
						}
					}
				}
			}
			else
				exit;
			
					
			
		} catch (Exception $e) {
			exit('getArrayOfTimesError: ' . $e->getMessage());
		}
	}
	
	
	
	
	
	
	private function convertAccents ($string) {
		return $string;//str_replace(array('\u00e9', '\u00e8', '\u00f4', '\u00ea'), array('e', 'e', 'o', 'e'), $string);
	}
	
	
	private function makeHTTPQuery ($host, $resource, $data) {
		$query  = "POST " . $resource . " HTTP/1.1\r\n";
		$query .= "Host: " . $host . "\r\n";
		$query .= "Connection: Close\r\n";
		$query .= "Content-type: application/x-www-form-urlencoded\r\n";
		$query .= "Content-Length: ".strlen($data)."\r\n\r\n";
		$query .= $data."\r\n";
		return $query;
	}
	
	
	
	
	
	private function parseHtmlTableToPhpArray ($html) {
		if (substr_count($html, "<tr>") != substr_count($html,"</tr>") || substr_count($html, "<td>") != substr_count($html,"</td>"))
			return array();
			
		$html = trim(substr($html, 7, count($html) - 9));
		$nbLines = substr_count($html, "<tr>");
		$htmlCopy = $html;
		$temp = 0;
		$array = array();
	
		while($temp < $nbLines) {
			$endLine = strpos($htmlCopy, "</tr>") - 4;
			$lines[$temp] = trim(substr($htmlCopy, 4, $endLine));
			$htmlCopy = substr(trim($htmlCopy), $endLine + 9);
			$temp++;
		}
	
		$temp = 0;
		while ($temp < $nbLines) {
			$nbColumns = substr_count($lines[$temp], "<td>");
			$lineCopy = trim($lines[$temp]);
			$temp2 = 0;
			
			while($temp2 < $nbColumns) {
				$endColumn = strpos($lineCopy, "</td>") - 4;
				$beginColumn = strpos($lineCopy, "<td>");
				$array[$temp][$temp2] = trim(substr($lineCopy, $beginColumn + 4, $endColumn));
				$lineCopy = substr(trim($lineCopy), $endColumn + 9);
				$temp2++;
			}
			$temp++;
		}
		return $array;
	}
	
	
	
	
}


class Date {
	
	private $day;
	private $month;
	private $year;
	
	public function __construct ($date = 0) {
		if (!$date)
			$date = date('d/m/Y');
			
		$temp = explode("/", $date);
		$this->setDay($temp[0]);
		$this->setMonth($temp[1]);
		$this->setYear($temp[2]);
	}

	public function setDay ($day) {
		$this->day = $day;
	}
	
	public function setMonth ($month) {
		$this->month = $month;
	}
	
	public function setYear ($year) {
		$this->year = $year;
	}
	
	public function getDay() {
		return $this->day;
	}
	
	public function getMonth () {
		return $this->month;
	}
	
	public function getYear () {
		return $this->year;
	}
	
	
	public function getDate () {
		return $this->day . '/' . $this->month . '/' . $this->year;
	}
	
}
?>