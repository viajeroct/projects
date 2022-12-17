#include <iostream>
#include <cmath>

typedef long double ld;

ld function(ld x, ld y, ld z) {
	int n = 10000000*2;
	for (int i = 0; i < n; i++) {
		ld new_x = sin(x*y - 2*y + z) + rand()%10;
		ld new_y = cos(x - 3*y*z + 4*z) + rand()%10;
		ld new_z = sin(5*x + y - z*y) + rand()%10;
		x = new_x;
		y = new_y;
		z = new_z;
	}
	return (x + y + z) / 2;
}

int main() {
	ld x, y, z;
	std::cin >> x >> y >> z;
	std::cout << function(x, y, z) << std::endl;
	
	return 0;
}
