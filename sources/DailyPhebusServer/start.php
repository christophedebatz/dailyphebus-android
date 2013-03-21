<?php

include_once ('./phebus.class.php');

if (!isset($_GET['access']) || $_GET['access']  != 'camchou')
	exit;

$bus = new Phebus; // on débute avec le sens opposé

if (isset($_GET['mode'])) {
	switch ($_GET['mode']) {
		case 'getBusLines':
			$bus->sendBusLines();
		break;
		case 'getBusStops':
			if (isset($_GET['letter'])) {
				$bus->setBusLineByLetter($_GET['letter']);
				$bus->sendBusStops();
			}
		break;
		case 'getBusSchedules':
			if (isset($_GET['letter'], $_GET['codeBusStop'], $_GET['date'], $_GET['hour'], $_GET['direction'])) {
				$bus->setBusLineByLetter($_GET['letter']);
				$bus->setBusStop($_GET['codeBusStop']);
				$bus->setDirection(intval($_GET['direction']));
				$bus->setDate(new Date($_GET['date']));
				if ($_GET['hour'] == '')
					$bus->getArrayOfTimes(Phebus::showAll);
				else
					$bus->getArrayOfTimes(intval($_GET['hour']));
			}
		break;
		default:
			echo '';
		break;
	}
}
?>