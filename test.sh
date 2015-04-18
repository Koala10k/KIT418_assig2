#!/usr/bin/sh


if [[ "$MPJ_HOME" == "/home/mpiuser/pengd/mpj" ]];
then
	echo "\$MPJ_HOME has been set up.";
else
	echo "reset \$MPJ_HOME.";
	export MPJ_HOME=/home/mpiuser/pengd/mpj;
fi

java -jar mpj/lib/starter.jar -np 3 -cp .:bin/ edu.utas.kit418.pengd.ParallelMatrixMultiply
