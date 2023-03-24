import { Component, OnInit } from '@angular/core';
import {Horse} from '../../../dto/horse';
import {Sex} from '../../../dto/sex';
import {HorseService} from '../../../service/horse.service';
import {OwnerService} from '../../../service/owner.service';
import {ActivatedRoute, Router} from '@angular/router';
import {ToastrService} from 'ngx-toastr';

@Component({
  selector: 'app-family-tree',
  templateUrl: './family-tree.component.html',
  styleUrls: ['./family-tree.component.scss']
})
export class FamilyTreeComponent implements OnInit {

  numberOfGenerations = 0;
  tempNumber = 0;

  horse: Horse = {
    name: '',
    description: '',
    dateOfBirth: new Date(),
    sex: Sex.female,
  };

  constructor(
    private service: HorseService,
    private ownerService: OwnerService,
    private router: Router,
    private route: ActivatedRoute,
    private notification: ToastrService,
  ) {
  }

  ngOnInit(): void {
    this.route.data.subscribe(() => {
      this.route.params.subscribe(params => this.horse.id = params.id);
    });
    let horseId = 0;
    this.route.params.subscribe(data => {
      horseId = data.id;
    });
    this.service.getById(horseId).subscribe(data =>{
      this.horse = data;
    });
  }

  load(){
    if (this.tempNumber < 0){
      this.numberOfGenerations = 0;
      this.tempNumber = 0;
    } else {
      this.numberOfGenerations = this.tempNumber;
    }
  }
  delete(id: number){
    return this.service.delete(id).subscribe({
      next: () => {
        this.notification.success('Horse deleted successfully');
        this.router.navigate(['horses']);
      },
      error: err => {
        this.notification.error('Deleting failed', err);
      }
    });
  }
}
