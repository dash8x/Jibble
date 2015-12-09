
<!DOCTYPE html>
<html lang="en">
	<head>
		<meta charset="utf-8">
		<title>PHP Test</title>
	</head>
	<body>
		<h1><?php echo "PHP is working!"; ?></h1>
		<ul>
			<?php for ( $i = 0; $i < 10; $i++ ) : ?>
			<li><?php echo "Item $i"; ?></li>
			<?php endfor; ?>
		</ul>
		<?php echo var_dump($_SERVER ); ?>
	</body>
</html>