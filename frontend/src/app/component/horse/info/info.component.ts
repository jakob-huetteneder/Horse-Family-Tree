import {Component, OnInit} from '@angular/core';
import {HorseService} from '../../../service/horse.service';
import {OwnerService} from '../../../service/owner.service';
import {ActivatedRoute, Router} from '@angular/router';
import {ToastrService} from 'ngx-toastr';
import {Horse} from '../../../dto/horse';
import {Sex} from '../../../dto/sex';
import {Owner} from '../../../dto/owner';


@Component({
  selector: 'app-info',
  templateUrl: './info.component.html',
  styleUrls: ['./info.component.scss']
})
export class InfoComponent implements OnInit {

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

  dateOfBirthAsLocaleDate(horse: Horse): string {
    return new Date(horse.dateOfBirth).toLocaleDateString();
  }

  public formatOwnerName(owner: Owner | null | undefined): string {
    return (owner == null)
      ? ''
      : `${owner.firstName} ${owner.lastName}`;
  }

  public formatHorseName(horse: Horse | null | undefined): string {
    return (horse == null)
      ? ''
      : `${horse.name}`;
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
