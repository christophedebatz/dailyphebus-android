<?php
$curpath = 'work/android/dailyphebus/';
$active = 'dpa';
$title = 'Daily Phebus';


$path = '';
for ($i = 0; $i < substr_count($curpath, '/'); $i++)
  $path .= '../';
include($path . 'header.php');
?>


<div class="span9">

  <ul class="breadcrumb">
  <li><a href="<?php echo $path; ?>">Labs</a> <span class="divider">/</span></li>
  <li><a href="../../">Travaux</a> <span class="divider">/</span></li>
  <li class="active">Android <span class="divider">/</span></li>
  <li class="active"><?php echo $title; ?></li>
</ul>

  <div class="page-header">
  <h1><?php echo $title; ?> <small>Android</small></h1>
</div>

  <div class="tabbable tabs-left">
  <ul class="nav nav-tabs">
    <li class="active"><a href="#tab1" data-toggle="tab">Résumé</a></li>
    <li><a href="#tab2" data-toggle="tab">Captures</a></li>
    <li><a href="#tab3" data-toggle="tab">Documents</a></li>
    <li><a href="#tab4" data-toggle="tab">Sources</a></li>
  </ul>
  <div class="tab-content">
    <div class="tab-pane active" id="tab1">

      <p><span class="label label-info">Description</span></p>
      <p>Daily Phebus est une application Android gratuite permettant d'accéder aux horaires du réseau de bus Versaillais (de la société Phébus). L'application ne contient aucune base de donnée native mais se connecte à plusieurs serveurs afin de retrouver les informations. Le tout est donc modulable et sans besoin de maintenance. Un projet de cache en SQLite est à l'étude.

      <ul type="disc">
          <li><a href="../../windows/dailyphebus/">Existe en version bureau sous Windows</a></li>
        </ul>
      </p>
      
      <p>&nbsp;</p>
      <p><span class="label label-info">Configuration requise</span></p>
      <p><span class="badge">Android 2.2+</span> <span class="badge">Internet</span></p>

      <p>&nbsp;</p>
      <p><span class="label label-info">Technologies utilisées</span></p>
      <p><span class="badge">Eclipse</span> <span class="badge">Java Android</span> <span class="badge">XML</span> <span class="badge">JSON</span> 
        <span class="badge">Adobe Dreamweaver</span> <span class="badge">PHP5 (POO)</span> <span class="badge">Curl</span></p>

      <p>&nbsp;</p>
      <span class="label label-info">Principales fonctionnalités</span> 
        <p><div style="padding-left: 30px;">
          <p><span class="badge">1</span> L'utilisateur demande des informations (horaires, lignes, arrêts...)</p>
          <p><span class="badge">2</span> L'application se connecte et envoi une requête au serveur</p>
          <p><span class="badge">3</span> Le serveur appelle une page du site Internet de Phebus</p>
          <p><span class="badge">4</span> Le serveur reçoit les données de Phebus et les parse en JSON (normalisation)</p>
          <p><span class="badge">5</span> Les données JSON sont renvoyées à l'application</p>
          <p><span class="badge">6</span> L'application affiche les informations sur l'interface depuis le JSON</p>
      </div>
    </p>

    <p>&nbsp;</p>
    <p><span class="label label-info">Licence</span></p>
      <p>Licence fermée propriétaire - droit d'auteur et de propriété intellectuelle - vous n'avez pas le droit de distribuer cette application et ses sources à des tiers. Ces contenus n'ont pas le droit d'être vendu sans autorisation écrite de son auteur. Vous n'avez pas le droit de modifier cette application dans le but de la copier illégalement. Pour toute information sur la licence, veuillez me contacter.</p>

      <p>&nbsp;</p>
    <p><span class="label label-info">Version</span></p>
      <p><span class="badge">1.2</span></p>
    </div>

    <div class="tab-pane" id="tab2">

      <ul class="thumbnails">
        
          
          <?php 
          $dirname = 'screens/';
          $dir = opendir($dirname); 

          while($file = readdir($dir)) {
            if($file != '.' && $file != '..' && !is_dir($dirname.$file))
            {
              $ext = pathinfo($file,  PATHINFO_EXTENSION);

              if(in_array($ext, array('gif', 'jpeg', 'jpg', 'png'))):
                ?>
              <li class="span2">
                <a class="thumbnail fancybox-buttons" data-fancybox-group="button" href="<?php echo $dirname . $file; ?>">
                  <img class="img-rounded" src="<?php echo $dirname . $file; ?>" alt="" />
                </a>
              </li>
                <?php
              endif;
            }
          }

        closedir($dir);
      ?>

      </ul>

    </div>

    <div class="tab-pane" id="tab3">
        <p><img src="<?php echo $path; ?>assets/img/pdf.gif" /> <a href="report/Introduction_Daily_Phebus pour_Android.pdf">Document technique</a> FR</p>
        <p><img src="<?php echo $path; ?>assets/img/apk.jpg" /> <a href="sources/DailyPhebus1-2.apk">Application Android</a> FR</p>
    </div>
    
    <div class="tab-pane" id="tab4">
      <p><img src="<?php echo $path; ?>assets/img/github.jpg" /> <a href="#" onclick="window.open('https://github.com/christophedebatz/dailyphebus/tree/master/sources');">Repository sur GitHub</a></p>
      <p><img src="<?php echo $path; ?>assets/img/zip.jpg" /> <a href="sources/dailyphebus.zip">Archive ZIP</a></p>
    </div>
  </div>
</div>

</div>
  
        

<?php
  include('../../../footer.php');
?>
