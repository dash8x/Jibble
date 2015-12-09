
<?php if (isset($_SERVER['Content'])) {
	 parse_str($_SERVER["Content"], $_POST);
}
?>
<!DOCTYPE html>
<html lang="en">
	<head>
		<meta charset="utf-8">
		<title>PHP Test</title>
	</head>
	<body>			
		<h1><?php echo "PHP is working!"; ?></h1>
		<?php if (empty($_POST)) : ?>
			<strong>Nothing posted</strong></br>
		<?php else : ?>
			<?php if (isset($_POST['name'])) : ?>
				<strong>Name:</strong> <?php echo $_POST['name']; ?></br>
			<?php endif; ?>
			<?php if (isset($_POST['address'])) : ?>
				<strong>Address:</strong> <?php echo $_POST['address']; ?></br>
			<?php endif; ?>
			<?php if (isset($_POST['options'])) : ?>
				<strong>Options:</strong> <?php echo implode(',', $_POST['options']); ?></br>
			<?php endif; ?>
		<?php endif; ?>			
	</body>
</html>