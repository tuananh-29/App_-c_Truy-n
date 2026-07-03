<?php

namespace Database\Seeders;

use Illuminate\Database\Seeder;
use App\Models\Customer;
use App\Models\Employee;
use Carbon\Carbon;

class DataSeeder extends Seeder
{
    public function run(): void
    {
        // Bơm dữ liệu mẫu cho Khách hàng (Customers) rải rác các tháng
        Customer::create(['name' => 'Nguyễn Văn A', 'email' => 'a@gmail.com', 'phone' => '0901234567', 'created_at' => Carbon::now()->subMonths(2)]);
        Customer::create(['name' => 'Trần Thị B', 'email' => 'b@gmail.com', 'phone' => '0907654321', 'created_at' => Carbon::now()->subMonth()]);
        Customer::create(['name' => 'Lê Văn C', 'email' => 'c@gmail.com', 'phone' => '0911223344', 'created_at' => Carbon::now()]);
        Customer::create(['name' => 'Phạm Minh Đạt', 'email' => 'dat@gmail.com', 'phone' => '0988776655', 'created_at' => Carbon::now()]);
        Customer::create(['name' => 'Hoàng Thanh Tùng', 'email' => 'tung@gmail.com', 'phone' => '0933445566', 'created_at' => Carbon::now()]);

        // Bơm dữ liệu mẫu cho Nhân viên (Employees)
        Employee::create(['name' => 'Lương Anh Tú', 'email' => 'anhtu.admin@gmail.com', 'position' => 'Admin Manager']);
        Employee::create(['name' => 'Nguyễn Biên Tập', 'email' => 'editor1@gmail.com', 'position' => 'Editor']);
        Employee::create(['name' => 'Trần Hỗ Trợ', 'email' => 'support1@gmail.com', 'position' => 'Support Staff']);
    }
}